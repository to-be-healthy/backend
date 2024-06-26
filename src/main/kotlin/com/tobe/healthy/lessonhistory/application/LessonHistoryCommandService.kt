package com.tobe.healthy.lessonhistory.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CopyObjectRequest
import com.tobe.healthy.common.FileUpload.FILE_MAXIMUM_UPLOAD_SIZE
import com.tobe.healthy.common.FileUpload.FILE_TEMP_UPLOAD_TIMEOUT
import com.tobe.healthy.common.Utils.*
import com.tobe.healthy.common.error.CustomException
import com.tobe.healthy.common.error.ErrorCode.*
import com.tobe.healthy.common.event.CustomEventPublisher
import com.tobe.healthy.common.event.EventType.NOTIFICATION
import com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI
import com.tobe.healthy.common.redis.RedisService
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommandRegisterComment
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommandRegisterLessonHistory
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommandUpdateComment
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommandUpdateLessonHistory
import com.tobe.healthy.lessonhistory.domain.dto.out.*
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles
import com.tobe.healthy.lessonhistory.repository.LessonHistoryCommentRepository
import com.tobe.healthy.lessonhistory.repository.LessonHistoryFilesRepository
import com.tobe.healthy.lessonhistory.repository.LessonHistoryRepository
import com.tobe.healthy.log
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.notification.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.notification.domain.entity.NotificationCategory.SCHEDULE
import com.tobe.healthy.notification.domain.entity.NotificationType
import com.tobe.healthy.notification.domain.entity.NotificationType.*
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
@Transactional
class LessonHistoryCommandService(
    private val lessonHistoryRepository: LessonHistoryRepository,
    private val lessonHistoryFilesRepository: LessonHistoryFilesRepository,
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val lessonHistoryCommentRepository: LessonHistoryCommentRepository,
    private val amazonS3: AmazonS3,
    private val redisService: RedisService,
    @Value("\${aws.s3.bucket-name}")
    private val bucketName: String,
    private val notificationPublisher: CustomEventPublisher<CommandSendNotification>,
) {

    fun registerLessonHistory(
        request: CommandRegisterLessonHistory,
        trainerId: Long
    ): CommandRegisterLessonHistoryResult {

        val student = findMember(request.studentId)
        val trainer = findMember(trainerId)
        val schedule = findSchedule(request.scheduleId)

        if (LocalDateTime.now().isBefore(LocalDateTime.of(schedule.lessonDt, schedule.lessonEndTime))) {
            throw IllegalArgumentException("수업이 끝나기 전에 수업일지를 작성할 수 없습니다.")
        }

        if (lessonHistoryRepository.validateDuplicateLessonHistory(trainerId, student.id, schedule.id)) {
            throw IllegalArgumentException("이미 수업일지를 등록하였습니다.")
        }

        val lessonHistory = LessonHistory.register(request.title, request.content, student, trainer, schedule)
        lessonHistoryRepository.save(lessonHistory)

        val files = registerFiles(request.uploadFiles, trainer, lessonHistory)

        // 학생에게 수업일지 작성 알림
        sendNotification(WRITE, WRITE.content, lessonHistory.id!!, lessonHistory.student!!.id!!, "https://www.to-be-healthy.site/student/log/${lessonHistory.id}")

        return CommandRegisterLessonHistoryResult.from(lessonHistory, files)
    }

    private fun sendNotification(
        notificationType: NotificationType,
        content: String,
        lessonHistoryId: Long,
        memberId: Long,
        clickUrl: String? = null
    ) {
        val notification = CommandSendNotification(
            title = notificationType.description,
            content = content,
            receiverIds = listOf(memberId),
            notificationType = notificationType,
            notificationCategory = SCHEDULE,
            targetId = lessonHistoryId,
            clickUrl = clickUrl
        )

        notificationPublisher.publish(notification, NOTIFICATION)
    }

    fun registerFilesOfLessonHistory(
        uploadFiles: MutableList<MultipartFile>,
        memberId: Long
    ): List<CommandUploadFileResult> {

        val commandUploadFileResult = mutableListOf<CommandUploadFileResult>()

        var fileOrder = 1

        uploadFiles.let {
            checkMaximumFileCount(it.size)

            for (uploadFile in it) {
                if (!uploadFile.isEmpty) {
                    val fileUrl = putFile(uploadFile)
                    commandUploadFileResult.add(
                        CommandUploadFileResult(
                            fileUrl = fileUrl,
                            fileOrder = fileOrder++
                        )
                    )
                    redisService.setValuesWithTimeout(
                        TEMP_FILE_URI.description + fileUrl,
                        memberId.toString(),
                        FILE_TEMP_UPLOAD_TIMEOUT.description.toLong()
                    ) // 30분
                }
            }
        }
        return commandUploadFileResult
    }

    fun updateLessonHistory(
        lessonHistoryId: Long,
        request: CommandUpdateLessonHistory,
        trainerId: Long
    ): CommandUpdateLessonHistoryResult {
        val findLessonHistory = lessonHistoryRepository.findOneLessonHistoryWithFiles(lessonHistoryId, trainerId)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        findLessonHistory.updateLessonHistory(request.title, request.content)

        findLessonHistory.files.clear()

        val files = registerFile(
            uploadFiles = request.uploadFiles,
            member = findLessonHistory.trainer!!,
            lessonHistory = findLessonHistory
        )

        return CommandUpdateLessonHistoryResult.from(findLessonHistory, files)
    }

    fun deleteLessonHistory(
        lessonHistoryId: Long,
        trainerId: Long
    ): Long {
        val lessonHistory = lessonHistoryRepository.findByIdAndTrainerId(lessonHistoryId, trainerId)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        deleteAllFiles(lessonHistory.files)

        lessonHistoryRepository.deleteById(lessonHistory.id!!)

        return lessonHistoryId
    }

    fun registerLessonHistoryComment(
        lessonHistoryId: Long,
        request: CommandRegisterComment,
        member: CustomMemberDetails
    ): CommandRegisterCommentResult {
        val findMember = findMember(member.memberId)

        val lessonHistory = lessonHistoryRepository.findById(lessonHistoryId, member.memberId, member.memberType)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        val order = lessonHistoryCommentRepository.findTopComment(lessonHistory.id)
        val lessonHistoryComment = registerComment(order, request, findMember, lessonHistory)
        val files = registerFile(request.uploadFiles, findMember, lessonHistory, lessonHistoryComment)

        // 게시글 작성자에게 알림 (내가 작성한 글은 알림을 받지 않음)
        if (member.memberId != lessonHistory.trainer!!.id) {
            sendNotification(COMMENT, COMMENT.content, lessonHistory.id!!, lessonHistory.trainer!!.id!!, "https://www.to-be-healthy.site/student/log/${lessonHistory.id}")
        }

        return CommandRegisterCommentResult.from(lessonHistoryComment, files)
    }

    fun registerLessonHistoryReply(
        lessonHistoryId: Long,
        lessonHistoryCommentId: Long,
        request: CommandRegisterComment,
        member: CustomMemberDetails
    ): CommandRegisterReplyResult {
        val findMember = findMember(member.memberId)

        val lessonHistory = lessonHistoryRepository.findById(lessonHistoryId, member.memberId, member.memberType)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        val order = lessonHistoryCommentRepository.findTopComment(lessonHistory.id, lessonHistoryCommentId)

        val parentComment = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId)
            ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)

        val entity = LessonHistoryComment(
            order = order,
            content = request.content!!,
            writer = findMember,
            lessonHistory = lessonHistory,
            parent = parentComment
        )

        // 댓글 작성자에게 알림 (내가 작성한 글은 알림을 받지 않음)
        if (lessonHistory.trainer!!.id != member.memberId) {
            sendNotification(REPLY, REPLY.content, lessonHistory.id!!, parentComment.writer?.id!!, "https://www.to-be-healthy.site/student/log/${lessonHistory.id}")
        }

        lessonHistoryCommentRepository.save(entity)

        val files = registerFile(
            request.uploadFiles,
            findMember,
            lessonHistory,
            entity
        )

        return CommandRegisterReplyResult.from(entity, files)
    }

    fun updateLessonHistoryComment(
        lessonHistoryCommentId: Long,
        request: CommandUpdateComment,
        member: CustomMemberDetails
    ): CommandUpdateCommentResult {
        val comment = lessonHistoryCommentRepository.findLessonHistoryCommentWithFiles(lessonHistoryCommentId, member.memberId)
            ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)

        val savedFiles = mutableListOf<LessonHistoryFiles>()

        // 파일 전체 삭제
        if (request.uploadFiles.isNotEmpty()) {
            request.uploadFiles.forEachIndexed { idx, file ->
                if (comment.files.indexOfFirst { it.fileUrl == file.fileUrl } != - 1) {
                    val fileIdx = comment.files.indexOfFirst { it.fileUrl == file.fileUrl }
                    comment.files[fileIdx].updateFileOrder(idx + 1)
                    savedFiles.add(comment.files[fileIdx])
                } else {
                    if (file.fileUrl.startsWith(S3_DOMAIN)) {
                        val tempUrl = file.fileUrl.replace(S3_DOMAIN, "")

                        val result = moveDirTempToOrigin("origin/lesson-history/", tempUrl, idx + 1)

                        val file = LessonHistoryFiles(
                            member = comment.writer,
                            lessonHistory = comment.lessonHistory,
                            lessonHistoryComment = comment,
                            fileUrl = result.fileUrl,
                            fileOrder = idx + 1,
                        )

                        savedFiles.add(file)
                    } else if (file.fileUrl.startsWith(CDN_DOMAIN)) {
                        val file = LessonHistoryFiles(
                            member = comment.writer,
                            lessonHistory = comment.lessonHistory,
                            lessonHistoryComment = comment,
                            fileUrl = file.fileUrl,
                            fileOrder = idx + 1,
                        )
                        savedFiles.add(file)
                    }
                }
            }
            lessonHistoryFilesRepository.deleteAll(comment.files)
            comment.files.clear()
            comment.files.addAll(savedFiles)
        } else {
            lessonHistoryFilesRepository.deleteAll(comment.files)
            comment.files.clear()
        }

        // 댓글 내용 업데이트
        comment.updateLessonHistoryComment(request.content)

        return CommandUpdateCommentResult.from(comment)
    }

    fun deleteLessonHistoryComment(
        lessonHistoryCommentId: Long,
        writerId: Long
    ): Long {
        val comment = lessonHistoryCommentRepository.findById(lessonHistoryCommentId, writerId)
            ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)

        deleteAllFiles(comment.files)

        comment.deleteComment()

        return lessonHistoryCommentId
    }

    private fun registerFile(
        uploadFiles: MutableList<CommandUploadFileResult>,
        member: Member,
        lessonHistory: LessonHistory,
        lessonHistoryComment: LessonHistoryComment? = null
    ): MutableList<LessonHistoryFiles> {

        checkMaximumFileCount(uploadFiles.size)

        val files = mutableListOf<LessonHistoryFiles>()

        uploadFiles.forEachIndexed { idx, uploadFile ->
            if (uploadFile.fileUrl.startsWith(S3_DOMAIN)) {
                val tempUrl = uploadFile.fileUrl.replace(S3_DOMAIN, "")

                val result = moveDirTempToOrigin("origin/lesson-history/", tempUrl, idx + 1)

                val file = LessonHistoryFiles(
                    member = member,
                    lessonHistory = lessonHistory,
                    lessonHistoryComment = lessonHistoryComment,
                    fileUrl = result.fileUrl,
                    fileOrder = result.fileOrder,
                )

                files.add(file)
            }
        }

        lessonHistoryFilesRepository.saveAll(files)

        return files
    }

    private fun registerComment(
        order: Int,
        request: CommandRegisterComment,
        findMember: Member,
        lessonHistory: LessonHistory
    ): LessonHistoryComment {

        val entity = LessonHistoryComment(
            order = order,
            content = request.content!!,
            writer = findMember,
            lessonHistory = lessonHistory
        )
        lessonHistoryCommentRepository.save(entity)
        return entity
    }

    private fun putFile(uploadFile: MultipartFile): String {
        val objectMetadata = createObjectMetadata(uploadFile.size, uploadFile.contentType)
        val savedFileName = createFileName("origin/lesson-history/", uploadFile.originalFilename!!.substring(uploadFile.originalFilename!!.lastIndexOf(".")))
        amazonS3.putObject(
            bucketName,
            savedFileName,
            uploadFile.inputStream,
            objectMetadata,
        )
        val fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString().replace(S3_DOMAIN, CDN_DOMAIN)

        log.info { "등록된 S3 파일 URL => ${fileUrl}" }
        return fileUrl
    }

    private fun findSchedule(scheduleId: Long?): Schedule {
        return trainerScheduleRepository.findByIdOrNull(scheduleId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
    }

    private fun registerFiles(
        uploadFiles: MutableList<CommandUploadFileResult>,
        member: Member,
        lessonHistory: LessonHistory
    ): MutableList<LessonHistoryFiles> {

        checkMaximumFileCount(uploadFiles.size)

        val files = mutableListOf<LessonHistoryFiles>()

        uploadFiles.forEachIndexed { idx, uploadFile ->
            if (uploadFile.fileUrl.startsWith(S3_DOMAIN)) {
                val tempUrl = uploadFile.fileUrl.replace(S3_DOMAIN, "")

                val result = moveDirTempToOrigin("origin/lesson-history/", tempUrl, idx + 1)

                val file = LessonHistoryFiles(
                    member = member,
                    lessonHistory = lessonHistory,
                    fileUrl = result.fileUrl,
                    fileOrder = result.fileOrder,
                )

                files.add(file)
            }
        }

        lessonHistoryFilesRepository.saveAll(files)

        return files
    }

    fun moveDirTempToOrigin(originDir: String, tempUrl: String, idx: Int): CommandUploadFileResult {
        val createdOriginUrl = originDir + tempUrl.replaceFirst("temp/".toRegex(), "")

        val copyObjRequest = CopyObjectRequest(
            bucketName,
            tempUrl,
            bucketName,
            createdOriginUrl
        )
        amazonS3.copyObject(copyObjRequest)

        val fileUrl = amazonS3.getUrl(bucketName, createdOriginUrl).toString().replace(S3_DOMAIN, CDN_DOMAIN)
        log.info { "등록한 fileUrl: ${fileUrl}" }

        return CommandUploadFileResult(fileUrl, idx)
    }

    private fun checkMaximumFileCount(uploadFilesSize: Int) {
        if (uploadFilesSize > FILE_MAXIMUM_UPLOAD_SIZE.description) {
            throw CustomException(EXCEED_MAXIMUM_NUMBER_OF_FILES)
        }
    }

    private fun findMember(memberId: Long?): Member {
        return memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)
    }

    private fun deleteAllFiles(files: MutableList<LessonHistoryFiles>) {
        files.forEach { file ->
            val fileName = getFileName(file.fileUrl)
            amazonS3.deleteObject(bucketName, fileName)
        }
        lessonHistoryFilesRepository.deleteAll(files)
    }

    private fun getFileName(url: String): String {
        val arr = url.split("/")
        return "origin/lesson-history/${arr.last()}"
    }
}
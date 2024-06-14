package com.tobe.healthy.lessonhistory.application

import com.amazonaws.services.s3.AmazonS3
import com.tobe.healthy.common.FileUpload.FILE_MAXIMUM_UPLOAD_SIZE
import com.tobe.healthy.common.FileUpload.FILE_TEMP_UPLOAD_TIMEOUT
import com.tobe.healthy.common.Utils.createFileName
import com.tobe.healthy.common.Utils.createObjectMetadata
import com.tobe.healthy.common.event.CustomEventPublisher
import com.tobe.healthy.common.event.EventType.NOTIFICATION
import com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI
import com.tobe.healthy.common.redis.RedisService
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
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
        val notification = CommandSendNotification(
            title = WRITE.description,
            content = String.format("트레이너가 새로운 수업일지를 작성하였습니다."),
            receiverIds = listOf(lessonHistory.student?.id!!),
            notificationType = WRITE,
            notificationCategory = SCHEDULE,
            targetId = lessonHistory.id
        )

        notificationPublisher.publish(notification, NOTIFICATION)

        return CommandRegisterLessonHistoryResult.from(lessonHistory, files)
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
            findMember = findLessonHistory.trainer!!,
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

        // 게시글 작성자에게 알림
        val notification = CommandSendNotification(
                title = COMMENT.description,
                content = String.format("내 게시글에 새로운 댓글이 달렸어요."),
                receiverIds = listOf(lessonHistory.trainer!!.id!!),
                notificationType = COMMENT,
                notificationCategory = SCHEDULE,
                targetId = lessonHistory.id
        )

        notificationPublisher.publish(notification, NOTIFICATION)

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

        // 댓글 작성자에게 알림
        val notification = CommandSendNotification(
            title = REPLY.description,
            content = String.format("내 댓글에 새로운 답글이 달렸어요."),
            receiverIds = listOf(parentComment.writer?.id!!),
            notificationType = REPLY,
            notificationCategory = SCHEDULE,
            targetId = lessonHistory.id
        )

        notificationPublisher.publish(notification, NOTIFICATION)

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

        deleteAllFiles(comment.files)

        comment.updateLessonHistoryComment(request.content, request.uploadFiles)

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
        findMember: Member,
        lessonHistory: LessonHistory,
        lessonHistoryComment: LessonHistoryComment? = null
    ): MutableList<LessonHistoryFiles> {

        val files = mutableListOf<LessonHistoryFiles>()

        uploadFiles.let {
            checkMaximumFileCount(it.size)
            for (uploadFile in it) {
                val file = LessonHistoryFiles(
                    member = findMember,
                    lessonHistory = lessonHistory,
                    lessonHistoryComment = lessonHistoryComment,
                    fileUrl = uploadFile.fileUrl,
                    fileOrder = uploadFile.fileOrder,
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
        val savedFileName = createFileName("lesson-history/")
        amazonS3.putObject(
            bucketName,
            savedFileName,
            uploadFile.inputStream,
            objectMetadata,
        )
        val fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString()
        log.info { "등록된 S3 파일 URL => ${fileUrl}" }
        return fileUrl
    }

    private fun findSchedule(scheduleId: Long?): Schedule {
        return trainerScheduleRepository.findByIdOrNull(scheduleId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
    }

    private fun registerFiles(
        uploadFiles: MutableList<CommandUploadFileResult>,
        trainer: Member,
        lessonHistory: LessonHistory
    ): MutableList<LessonHistoryFiles> {
        val files = mutableListOf<LessonHistoryFiles>()

        uploadFiles.let {
            checkMaximumFileCount(it.size)

            for (uploadFile in it) {
                val file = LessonHistoryFiles(
                    member = trainer,
                    lessonHistory = lessonHistory,
                    fileUrl = uploadFile.fileUrl,
                    fileOrder = uploadFile.fileOrder,
                )
                files.add(file)
                redisService.deleteValues(TEMP_FILE_URI.description + uploadFile.fileUrl)
            }

            lessonHistoryFilesRepository.saveAll(files)
        }
        return files
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
        files.forEach {
            it.fileUrl.let { fileUrl ->
                amazonS3.deleteObject(bucketName, fileUrl)
            }
        }
        lessonHistoryFilesRepository.deleteAll(files)
    }
}


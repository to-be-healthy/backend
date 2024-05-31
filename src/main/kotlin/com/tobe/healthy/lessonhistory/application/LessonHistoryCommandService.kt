package com.tobe.healthy.lessonhistory.application

import com.amazonaws.services.s3.AmazonS3
import com.tobe.healthy.common.FileUpload.FILE_MAXIMUM_UPLOAD_SIZE
import com.tobe.healthy.common.FileUpload.FILE_TEMP_UPLOAD_TIMEOUT
import com.tobe.healthy.common.Utils.createFileName
import com.tobe.healthy.common.Utils.createObjectMetadata
import com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI
import com.tobe.healthy.common.redis.RedisService
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
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
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

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
) {

    fun registerLessonHistory(
        request: CommandRegisterLessonHistory,
        trainerId: Long
    ): CommandRegisterLessonHistoryResult {
        val student = findMember(request.studentId)
        val trainer = findMember(trainerId)
        val schedule = findSchedule(request.scheduleId)

        if (lessonHistoryRepository.validateDuplicateLessonHistory(trainerId, student.id, schedule.id)) {
            throw IllegalArgumentException("이미 수업일지를 등록하였습니다.")
        }

        val lessonHistory = LessonHistory.register(request.title!!, request.content!!, student, trainer, schedule)
        lessonHistoryRepository.save(lessonHistory)

        val files = registerFiles(request.uploadFiles, trainer, lessonHistory)

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
        request: CommandUpdateLessonHistory
    ): CommandUpdateLessonHistoryResult {
        val findLessonHistory = lessonHistoryRepository.findOneLessonHistoryWithFiles(lessonHistoryId)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        findLessonHistory.updateLessonHistory(request.title!!, request.content!!)

        findLessonHistory.files.clear()

        val files = registerFile(
            uploadFiles = request.uploadFiles,
            findMember = findLessonHistory.trainer!!,
            lessonHistory = findLessonHistory
        )

        return CommandUpdateLessonHistoryResult.from(findLessonHistory, files)
    }

    fun deleteLessonHistory(
        lessonHistoryId: Long
    ): Long {
        val findLessonHistory = findLessonHistory(lessonHistoryId)

        findLessonHistory.let {
            deleteAllFiles(it.files)
            lessonHistoryRepository.deleteById(it.id!!)
        }

        return lessonHistoryId
    }

    fun registerLessonHistoryComment(
        lessonHistoryId: Long,
        request: CommandRegisterComment,
        memberId: Long
    ): CommandRegisterCommentResult {
        val findMember = findMember(memberId)

        val lessonHistory = findLessonHistory(lessonHistoryId)

        val order = lessonHistoryCommentRepository.findTopComment(lessonHistory.id)
        val lessonHistoryComment = registerComment(order, request, findMember, lessonHistory)
        val files = registerFile(request.uploadFiles, findMember, lessonHistory, lessonHistoryComment)

        return CommandRegisterCommentResult.from(lessonHistoryComment, files)
    }

    fun registerLessonHistoryReply(
        lessonHistoryId: Long,
        lessonHistoryCommentId: Long,
        request: CommandRegisterComment,
        memberId: Long
    ): CommandRegisterReplyResult {
        val findMember = findMember(memberId)

        val lessonHistory = findLessonHistory(lessonHistoryId)

        val order = lessonHistoryCommentRepository.findTopComment(lessonHistory.id, lessonHistoryCommentId)

        val parentComment = findLessonHistoryComment(lessonHistoryCommentId)

        val entity = LessonHistoryComment(
            order = order,
            content = request.content!!,
            writer = findMember,
            lessonHistory = lessonHistory,
            parent = parentComment
        )

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
        request: CommandUpdateComment
    ): CommandUpdateCommentResult {
        lessonHistoryCommentRepository.findLessonHistoryCommentWithFiles(lessonHistoryCommentId)
            ?.let {
                deleteAllFiles(it.files)

                it.updateLessonHistoryComment(
                    request.content!!,
                    request.uploadFiles
                )

                return CommandUpdateCommentResult.from(it)

            } ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)
    }

    fun deleteLessonHistoryComment(
        lessonHistoryCommentId: Long
    ): Long {

        val findLessonHistoryComment = findLessonHistoryComment(lessonHistoryCommentId)

        deleteAllFiles(findLessonHistoryComment.files)

        findLessonHistoryComment.deleteComment()

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

    private fun findLessonHistoryComment(lessonHistoryCommentId: Long): LessonHistoryComment {
        return lessonHistoryCommentRepository.findCommentById(lessonHistoryCommentId)
            ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)
    }

    private fun findLessonHistory(lessonHistoryId: Long?): LessonHistory {
        return lessonHistoryRepository.findByIdOrNull(lessonHistoryId)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
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


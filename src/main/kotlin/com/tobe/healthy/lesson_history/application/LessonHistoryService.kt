package com.tobe.healthy.lesson_history.application

import com.amazonaws.services.s3.AmazonS3
import com.tobe.healthy.common.CustomPagingResponse
import com.tobe.healthy.common.Utils
import com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI
import com.tobe.healthy.common.redis.RedisService
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.lesson_history.domain.dto.`in`.*
import com.tobe.healthy.lesson_history.domain.dto.out.LessonHistoryDetailResponse
import com.tobe.healthy.lesson_history.domain.dto.out.LessonHistoryResponse
import com.tobe.healthy.lesson_history.domain.dto.out.UploadFileResponse
import com.tobe.healthy.lesson_history.domain.entity.LessonHistory
import com.tobe.healthy.lesson_history.domain.entity.LessonHistoryComment
import com.tobe.healthy.lesson_history.domain.entity.LessonHistoryFiles
import com.tobe.healthy.lesson_history.repository.LessonHistoryCommentRepository
import com.tobe.healthy.lesson_history.repository.LessonHistoryFilesRepository
import com.tobe.healthy.lesson_history.repository.LessonHistoryRepository
import com.tobe.healthy.log
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class LessonHistoryService(
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

    fun registerLessonHistory(request: RegisterLessonHistoryCommand, trainerId: Long): Boolean {
        val (findMember, findTrainer, findSchedule) = checkLessonHistoryRequirements(request, trainerId)

        val lessonHistory = registerLessonHistory(request, findMember, findTrainer, findSchedule)

        registerFiles(request.uploadFileResponse, findTrainer, lessonHistory)

        return true
    }

    fun findAllLessonHistory(
        request: SearchCondRequest,
        pageable: Pageable,
        memberId: Long,
        memberType: MemberType
    ): CustomPagingResponse<LessonHistoryResponse> {
        val results = lessonHistoryRepository.findAllLessonHistory(request, pageable, memberId, memberType)
        return CustomPagingResponse(
            content = results.content,
            pageNumber = results.pageable.pageNumber,
            pageSize = results.pageable.pageSize,
            totalPages = results.totalPages,
            totalElements = results.totalElements,
            isLast = results.isLast,
        )
    }

    fun findOneLessonHistory(
        lessonHistoryId: Long,
        memberId: Long,
        memberType: MemberType
    ): LessonHistoryDetailResponse? {
        return lessonHistoryRepository.findOneLessonHistory(lessonHistoryId, memberId, memberType)
    }

    fun updateLessonHistory(lessonHistoryId: Long, request: LessonHistoryCommand): Boolean {
        val findLessonHistory = lessonHistoryRepository.findByIdOrNull(lessonHistoryId)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        findLessonHistory.updateLessonHistory(request.title!!, request.content!!)

        registerFile(
            uploadFiles = request.uploadFileResponse,
            findMember = findLessonHistory.trainer,
            lessonHistory = findLessonHistory,
            lessonHistoryComment = null,
        )

        return true
    }

    fun deleteLessonHistory(lessonHistoryId: Long): Boolean {
        val findLessonHistory = lessonHistoryRepository.findByIdOrNull(lessonHistoryId)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        lessonHistoryRepository.deleteById(findLessonHistory.id)
        return true
    }

    fun registerLessonHistoryComment(
        lessonHistoryId: Long,
        request: CommentRegisterCommand,
        memberId: Long,
    ): Boolean {
        val findMember = memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val lessonHistory = lessonHistoryRepository.findByIdOrNull(lessonHistoryId)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        val order = lessonHistoryCommentRepository.findTopComment(lessonHistory.id)
        val lessonHistoryComment = registerComment(order, request, findMember, lessonHistory)
        registerFile(request.uploadFileResponse, findMember, lessonHistory, lessonHistoryComment)
        return true
    }

    fun registerLessonHistoryReply(
        lessonHistoryId: Long,
        lessonHistoryCommentId: Long,
        request: CommentRegisterCommand,
        memberId: Long,
    ): Boolean {
        val findMember = memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val lessonHistory = lessonHistoryRepository.findByIdOrNull(lessonHistoryId)
            ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        val order = lessonHistoryCommentRepository.findTopComment(
            lessonHistory.id,
            lessonHistoryCommentId
        )

        val parentComment = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId)
            ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)

        val entity = LessonHistoryComment(
            order = order,
            content = request.comment!!,
            writer = findMember,
            lessonHistory = lessonHistory,
            parent = parentComment
        )

        lessonHistoryCommentRepository.save(entity)
        registerFile(request.uploadFileResponse, findMember, lessonHistory, entity)
        return true
    }

    fun updateLessonHistoryComment(
        lessonHistoryCommentId: Long,
        request: LessonHistoryCommentCommand
    ): Boolean {
        val comment = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId)
            ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)

        comment.updateLessonHistoryComment(request.comment)

        return true
    }

    fun deleteLessonHistoryComment(lessonHistoryCommentId: Long): Boolean {

        val findLessonHistoryComment = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId)
                ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)

        findLessonHistoryComment.deleteComment()

        return true
    }

    private fun registerFiles(
        uploadFiles: MutableList<UploadFileResponse>?,
        findMember: Member,
        lessonHistory: LessonHistory
    ) {
        uploadFiles?.let {

            checkMaximumFileSize(it.size)

            for (uploadFile in it) {
                val file = LessonHistoryFiles(
                    member = findMember,
                    lessonHistory = lessonHistory,
                    fileUrl = uploadFile.fileUrl,
                    fileOrder = uploadFile.fileOrder,
                )

                lessonHistoryFilesRepository.save(file)

                redisService.deleteValues(TEMP_FILE_URI.description + uploadFile.fileUrl)
            }
        }
    }

    private fun checkLessonHistoryRequirements(
        request: RegisterLessonHistoryCommand,
        trainerId: Long
    ): Triple<Member, Member, Schedule> {

        val findMember = memberRepository.findByIdOrNull(request.studentId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val findTrainer = memberRepository.findByIdOrNull(trainerId)
            ?: throw CustomException(TRAINER_NOT_FOUND)

        val findSchedule = trainerScheduleRepository.findByIdOrNull(request.scheduleId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)

        return Triple(findMember, findTrainer, findSchedule)
    }

    private fun registerLessonHistory(
        request: RegisterLessonHistoryCommand,
        findMember: Member,
        findTrainer: Member,
        findSchedule: Schedule
    ): LessonHistory {
        val lessonHistory = LessonHistory.register(request, findMember, findTrainer, findSchedule)
        lessonHistoryRepository.save(lessonHistory)
        return lessonHistory
    }

    private fun registerComment(
        order: Int,
        request: CommentRegisterCommand,
        findMember: Member,
        lessonHistory: LessonHistory
    ): LessonHistoryComment {
        val entity = LessonHistoryComment(
            order = order,
            content = request.comment!!,
            writer = findMember,
            lessonHistory = lessonHistory,
        )
        lessonHistoryCommentRepository.save(entity)
        return entity
    }

    private fun registerFile(
        uploadFiles: MutableList<UploadFileResponse>?,
        findMember: Member,
        lessonHistory: LessonHistory?,
        lessonHistoryComment: LessonHistoryComment?
    ) {

        uploadFiles?.let {
            checkMaximumFileSize(it.size)
            for (uploadFile in it) {
                val file = LessonHistoryFiles(
                    member = findMember,
                    lessonHistory = lessonHistory,
                    lessonHistoryComment = lessonHistoryComment,
                    fileUrl = uploadFile.fileUrl,
                    fileOrder = uploadFile.fileOrder,
                )
                lessonHistoryFilesRepository.save(file)
            }
        }
    }

    private fun putFile(uploadFile: MultipartFile): String {
        val objectMetadata = Utils.createObjectMetadata(uploadFile.size, uploadFile.contentType)
        val savedFileName = "lesson-history/" + Utils.createFileUUID()
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

    private fun checkMaximumFileSize(uploadFilesSize: Int) {
        if (uploadFilesSize > FILE_MAXIMUM_UPLOAD_SIZE) {
            throw CustomException(EXCEED_MAXIMUM_NUMBER_OF_FILES)
        }
    }

    fun findAllLessonHistoryByMemberId(
        studentId: Long,
        request: SearchCondRequest,
        pageable: Pageable
    ): CustomPagingResponse<LessonHistoryResponse> {
        val findMember = memberRepository.findByIdOrNull(studentId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val results = lessonHistoryRepository.findAllLessonHistoryByMemberId(findMember.id, request, pageable)

        return CustomPagingResponse(
            findMember.name,
            results.content,
            results.pageable.pageNumber,
            results.pageable.pageSize,
            results.totalPages,
            results.totalElements,
            results.isLast,
        )
    }

    fun registerFilesOfLessonHistory(
        uploadFiles: MutableList<MultipartFile>,
        memberId: Long
    ): List<UploadFileResponse> {
        val uploadFileResponse: MutableList<UploadFileResponse> = mutableListOf()
        var fileOrder = 1
        uploadFiles.let {
            checkMaximumFileSize(it.size)

            for (uploadFile in it) {
                if (!uploadFile.isEmpty) {
                    val fileUrl = putFile(uploadFile)
                    uploadFileResponse.add(
                        UploadFileResponse(
                            fileUrl = fileUrl,
                            fileOrder = fileOrder++,
                        ),
                    )
                    redisService.setValuesWithTimeout(TEMP_FILE_URI.description + fileUrl, memberId.toString(), FILE_TEMP_UPLOAD_TIMEOUT) // 30분
                }
            }
        }
        return uploadFileResponse
    }

    companion object {
        const val FILE_MAXIMUM_UPLOAD_SIZE = 3
        const val FILE_TEMP_UPLOAD_TIMEOUT: Long = 30 * 60 * 1000 // 30분
    }
}

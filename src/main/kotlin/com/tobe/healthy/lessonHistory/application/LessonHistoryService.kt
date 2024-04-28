package com.tobe.healthy.lessonHistory.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.file.domain.entity.AwsS3File
import com.tobe.healthy.file.repository.AwsS3FileRepository
import com.tobe.healthy.lessonHistory.domain.dto.`in`.*
import com.tobe.healthy.lessonHistory.domain.dto.out.LessonHistoryDetailResponse
import com.tobe.healthy.lessonHistory.domain.dto.out.LessonHistoryResponse
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonHistory.repository.LessonHistoryCommentRepository
import com.tobe.healthy.lessonHistory.repository.LessonHistoryRepository
import com.tobe.healthy.log
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.repository.ScheduleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class LessonHistoryService(
    private val lessonHistoryRepository: LessonHistoryRepository,
    private val memberRepository: MemberRepository,
    private val scheduleRepository: ScheduleRepository,
    private val lessonHistoryCommentRepository: LessonHistoryCommentRepository,
    private val awsS3FileRepository: AwsS3FileRepository,
    private val amazonS3: AmazonS3
) {

    fun registerLessonHistory(
        request: RegisterLessonHistoryCommand,
        uploadFiles: MutableList<MultipartFile>?,
        trainerId: Long
    ): Boolean {
        val (findMember, findTrainer, findSchedule) = checkLessonHistoryRequirements(
            request,
            trainerId
        )

        val lessonHistory = registerLessonHistory(request, findMember, findTrainer, findSchedule)

        registerFiles(uploadFiles, findMember, lessonHistory)
        return true
    }

    fun findAllLessonHistory(
        request: SearchCondRequest,
        pageable: Pageable,
        memberId: Long,
        memberType: MemberType
    ): Page<LessonHistoryResponse> {
        return lessonHistoryRepository.findAllLessonHistory(request, pageable, memberId, memberType)
    }

    fun findOneLessonHistory(
        lessonHistoryId: Long,
        memberId: Long,
        memberType: MemberType
    ): LessonHistoryDetailResponse {
        return lessonHistoryRepository.findOneLessonHistory(lessonHistoryId, memberId, memberType)
    }

    fun updateLessonHistory(lessonHistoryId: Long, request: LessonHistoryCommand): Boolean {
        val findEntity =
            lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(
                LESSON_HISTORY_NOT_FOUND
            )
        findEntity.updateLessonHistory(request.title!!, request.content!!)
        return true
    }

    fun deleteLessonHistory(lessonHistoryId: Long): Boolean {
        val findLessonHistory =
            lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(
                LESSON_HISTORY_NOT_FOUND
            )
        lessonHistoryRepository.deleteById(findLessonHistory.id)
        return true
    }

    fun registerLessonHistoryComment(
        lessonHistoryId: Long,
        uploadFiles: MutableList<MultipartFile>?,
        request: CommentRegisterCommand,
        memberId: Long
    ): Boolean {
        val findMember =
            memberRepository.findByIdOrNull(memberId) ?: throw CustomException(MEMBER_NOT_FOUND)
        val lessonHistory =
            lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(
                LESSON_HISTORY_NOT_FOUND
            )
        val order = lessonHistoryCommentRepository.findTopComment(lessonHistory.id)
        val entity = registerComment(order, request, findMember, lessonHistory)
        registerFile(uploadFiles, findMember, lessonHistory, entity)
        return true
    }

    fun registerLessonHistoryReply(
        lessonHistoryId: Long,
        lessonHistoryCommentId: Long,
        uploadFiles: MutableList<MultipartFile>?,
        request: CommentRegisterCommand,
        memberId: Long
    ): Boolean {
        val findMember =
            memberRepository.findByIdOrNull(memberId) ?: throw CustomException(MEMBER_NOT_FOUND)
        val lessonHistory =
            lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(
                LESSON_HISTORY_NOT_FOUND
            )
        val order = lessonHistoryCommentRepository.findTopComment(
            lessonHistory.id,
            lessonHistoryCommentId!!
        )
        val parentComment = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId)
        val entity = LessonHistoryComment(
            order = order,
            content = request.comment!!,
            writer = findMember,
            lessonHistory = lessonHistory,
            parentId = parentComment
        )
        lessonHistoryCommentRepository.save(entity)
        registerFile(uploadFiles, findMember, lessonHistory, entity)
        return true
    }

    fun updateLessonHistoryComment(
        lessonHistoryCommentId: Long,
        request: LessonHistoryCommentCommand
    ): Boolean {
        val comment = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId)
            ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)
        comment.updateLessonHistoryComment(request.content!!)
        return true
    }

    fun deleteLessonHistoryComment(lessonHistoryCommentId: Long): Boolean {
        val findLessonHistoryComment =
            lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId)
                ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)
        findLessonHistoryComment.deleteComment()
        return true
    }

    private fun registerFiles(
        uploadFiles: MutableList<MultipartFile>?,
        findMember: Member,
        lessonHistory: LessonHistory
    ) {
        uploadFiles?.let {
            var fileOrder = 1;
            checkMaximumFileSize(uploadFiles.size)

            for (uploadFile in it) {
                if (!uploadFile.isEmpty) {
                    val (originalFileName, fileUrl) = putFile(uploadFile)

                    val file = AwsS3File.builder()
                        .originalFileName(originalFileName)
                        .member(findMember)
                        .lessonHistory(lessonHistory)
                        .fileUrl(fileUrl)
                        .fileOrder(fileOrder++)
                        .build()

                    awsS3FileRepository.save(file)
                }
            }
        }
    }

    private fun checkLessonHistoryRequirements(
        request: RegisterLessonHistoryCommand,
        trainerId: Long
    ): Triple<Member, Member, Schedule> {
        val findMember =
            memberRepository.findByIdOrNull(request.studentId) ?: throw CustomException(
                MEMBER_NOT_FOUND
            )
        val findTrainer =
            memberRepository.findByIdOrNull(trainerId) ?: throw CustomException(TRAINER_NOT_FOUND)
        val findSchedule =
            scheduleRepository.findByIdOrNull(request.scheduleId) ?: throw CustomException(
                SCHEDULE_NOT_FOUND
            )
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

    private fun getObjectMetadata(uploadFile: MultipartFile): ObjectMetadata {
        val objectMetadata = ObjectMetadata()
        objectMetadata.contentLength = uploadFile.size
        objectMetadata.contentType = uploadFile.contentType
        return objectMetadata
    }

    private fun registerComment(
        order: Int,
        request: CommentRegisterCommand,
        findMember: Member,
        lessonHistory: LessonHistory,
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
        uploadFiles: MutableList<MultipartFile>?,
        findMember: Member,
        lessonHistory: LessonHistory,
        entity: LessonHistoryComment,
    ) {
        uploadFiles?.let {
            checkMaximumFileSize(it.size)
            var fileOrder = 1;
            for (uploadFile in it) {
                if (!uploadFile.isEmpty) {
                    val (originalFileName, fileUrl) = putFile(uploadFile)

                    val file = AwsS3File.builder()
                        .originalFileName(originalFileName)
                        .member(findMember)
                        .lessonHistory(lessonHistory)
                        .fileUrl(fileUrl)
                        .fileOrder(fileOrder++)
                        .lessonHistoryComment(entity)
                        .build()
                    awsS3FileRepository.save(file)
                }
            }
        }
    }

    private fun putFile(uploadFile: MultipartFile): Pair<String?, String> {
        val originalFileName = uploadFile.originalFilename
        val objectMetadata = getObjectMetadata(uploadFile)
        val extension = originalFileName?.substring(originalFileName.lastIndexOf("."))
        val savedFileName = System.currentTimeMillis().toString() + extension
        amazonS3.putObject(
            "to-be-healthy-bucket",
            savedFileName,
            uploadFile.inputStream,
            objectMetadata
        )
        val fileUrl = amazonS3.getUrl("to-be-healthy-bucket", savedFileName).toString()
        log.info { "등록된 S3 파일 URL => ${fileUrl}" }
        return Pair(originalFileName, fileUrl)
    }

    private fun checkMaximumFileSize(uploadFilesSize: Int) {
        if (uploadFilesSize > FILE_MAXIMUM_UPLOAD_SIZE) {
            throw CustomException(EXCEED_MAXIMUM_NUMBER_OF_FILES)
        }
    }

    fun findAllLessonHistoryByMemberId(
        studentId: Long?,
        request: SearchCondRequest,
        pageable: Pageable
    ): Page<LessonHistoryResponse> {
        val findMember =
            memberRepository.findByIdOrNull(studentId) ?: throw CustomException(MEMBER_NOT_FOUND)
        return lessonHistoryRepository.findAllLessonHistoryByMemberId(
            findMember.id,
            request,
            pageable
        )
    }

    companion object {
        const val FILE_MAXIMUM_UPLOAD_SIZE = 3
    }
}

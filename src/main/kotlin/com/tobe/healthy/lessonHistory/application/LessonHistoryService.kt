package com.tobe.healthy.lessonHistory.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.file.domain.entity.AwsS3File
import com.tobe.healthy.file.repository.AwsS3FileRepository
import com.tobe.healthy.lessonHistory.domain.dto.*
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonHistory.repository.LessonHistoryCommentRepository
import com.tobe.healthy.lessonHistory.repository.LessonHistoryRepository
import com.tobe.healthy.log
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.repository.MemberRepository
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

    fun registerLessonHistory(request: RegisterLessonHistoryCommand, uploadFiles: MutableList<MultipartFile>?, studentId: Long): Boolean {
        val findMember = memberRepository.findByIdOrNull(studentId) ?: throw CustomException(MEMBER_NOT_FOUND)

        val findTrainer = memberRepository.findByIdOrNull(request.trainer) ?: throw CustomException(TRAINER_NOT_FOUND)

        val findSchedule = scheduleRepository.findByIdOrNull(request.schedule) ?: throw CustomException(SCHEDULE_NOT_FOUND)

        val lessonHistory = LessonHistory.register(request, findMember, findTrainer, findSchedule)

        lessonHistoryRepository.save(lessonHistory)

        uploadFiles?.let {
            var fileOrder = 1;
            for (uploadFile in it) {
                if (!uploadFile.isEmpty) {
                    val originalFileName = uploadFile.originalFilename
                    val objectMetadata = getObjectMetadata(uploadFile)
                    val extension = originalFileName?.substring(originalFileName.lastIndexOf("."))
                    val savedFileName = System.currentTimeMillis().toString() + extension
                    amazonS3.putObject("to-be-healthy-bucket", savedFileName, uploadFile.inputStream, objectMetadata)
                    val fileUrl = amazonS3.getUrl("to-be-healthy-bucket", savedFileName).toString()
                    log.info { "fileUrl -> ${fileUrl}" }

                    val file = AwsS3File.create(
                        originalFileName,
                        findMember,
                        lessonHistory,
                        fileUrl,
                        fileOrder++,
                        null
                    )
                    awsS3FileRepository.save(file)
                }
            }
        }
        return true
    }

    private fun getObjectMetadata(uploadFile: MultipartFile): ObjectMetadata {
        val objectMetadata = ObjectMetadata()
        objectMetadata.contentLength = uploadFile.size
        objectMetadata.contentType = uploadFile.contentType
        return objectMetadata
    }

    fun findAllLessonHistory(request: SearchCondRequest, pageable: Pageable, memberId: Long, memberType: MemberType): Page<LessonHistoryCommandResult> {
        return lessonHistoryRepository.findAllLessonHistory(request, pageable, memberId, memberType)
    }

    fun findOneLessonHistory(lessonHistoryId: Long, memberId: Long, memberType: MemberType): List<LessonHistoryCommandResult> {
        return lessonHistoryRepository.findOneLessonHistory(lessonHistoryId, memberId, memberType)
    }

    fun updateLessonHistory(lessonHistoryId: Long, request: LessonHistoryUpdateCommand): Boolean {
        val findEntity = lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
        findEntity.updateLessonHistory(request.title, request.content)
        return true
    }

    fun updateLessonHistoryComment(lessonHistoryCommentId: Long, request: LessonHistoryCommentUpdateCommand): Boolean {
        val comment = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId) ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)
        comment.updateLessonHistoryComment(request.content!!)
        return true
    }

    fun deleteLessonHistory(lessonHistoryId: Long): Boolean {
        lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
        lessonHistoryRepository.deleteById(lessonHistoryId)
        return true
    }

    fun deleteLessonHistoryComment(lessonHistoryCommentId: Long): Boolean {
        lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId) ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)
        lessonHistoryCommentRepository.deleteById(lessonHistoryCommentId)
        return true
    }

    fun registerLessonHistoryComment(lessonHistoryId: Long, uploadFiles: MutableList<MultipartFile>?, request: CommentRegisterCommand, memberId: Long): Boolean {
        val findMember = memberRepository.findByIdOrNull(memberId) ?: throw CustomException(MEMBER_NOT_FOUND)
        val lessonHistory = lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
        val order = lessonHistoryCommentRepository.findTopComment(lessonHistoryId)
        val entity = LessonHistoryComment(
            order = order + 1,
            content = request.comment!!,
            writer = findMember,
            lessonHistory = lessonHistory
        )
        val comment = lessonHistoryCommentRepository.save(entity)
        uploadFiles?.let {
            var fileOrder = 1;
            for (uploadFile in it) {
                if (!uploadFile.isEmpty) {
                    val originalFileName = uploadFile.originalFilename
                    val objectMetadata = getObjectMetadata(uploadFile)
                    val extension = originalFileName?.substring(originalFileName.lastIndexOf("."))
                    val savedFileName = System.currentTimeMillis().toString() + extension
                    amazonS3.putObject("to-be-healthy-bucket", savedFileName, uploadFile.inputStream, objectMetadata)
                    val fileUrl = amazonS3.getUrl("to-be-healthy-bucket", savedFileName).toString()
                    log.info { "fileUrl -> ${fileUrl}" }

                    val file = AwsS3File.create(
                        originalFileName,
                        findMember,
                        lessonHistory,
                        fileUrl,
                        fileOrder++,
                        comment
                    )
                    awsS3FileRepository.save(file)
                }
            }
        }
        return true
    }

    fun registerLessonHistoryReply(lessonHistoryId: Long, lessonHistoryCommentId: Long, uploadFiles: MutableList<MultipartFile>?,
                                   request: CommentRegisterCommand, memberId: Long): Boolean {
        val findMember = memberRepository.findByIdOrNull(memberId) ?: throw CustomException(MEMBER_NOT_FOUND)
        val lessonHistory = lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
        val order = lessonHistoryCommentRepository.findTopComment(lessonHistoryId, lessonHistoryCommentId)
        val parentComment = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId)
        val entity = LessonHistoryComment(
            order = order + 1,
            content = request.comment!!,
            writer = findMember,
            lessonHistory = lessonHistory,
            parentId = parentComment
        )
        val comment = lessonHistoryCommentRepository.save(entity)
        uploadFiles?.let {
            var fileOrder = 1;
            for (uploadFile in it) {
                if (!uploadFile.isEmpty) {
                    val originalFileName = uploadFile.originalFilename
                    val objectMetadata = getObjectMetadata(uploadFile)
                    val extension = originalFileName?.substring(originalFileName.lastIndexOf("."))
                    val savedFileName = System.currentTimeMillis().toString() + extension
                    amazonS3.putObject("to-be-healthy-bucket", savedFileName, uploadFile.inputStream, objectMetadata)
                    val fileUrl = amazonS3.getUrl("to-be-healthy-bucket", savedFileName).toString()
                    log.info { "fileUrl -> ${fileUrl}" }

                    val file = AwsS3File.create(
                        originalFileName,
                        findMember,
                        lessonHistory,
                        fileUrl,
                        fileOrder++,
                        comment
                    )
                    awsS3FileRepository.save(file)
                }
            }
        }
        return true

    }
}
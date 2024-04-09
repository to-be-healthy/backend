package com.tobe.healthy.lessonHistory.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.file.domain.entity.AwsS3File
import com.tobe.healthy.file.repository.AwsS3FileRepository
import com.tobe.healthy.lessonHistory.domain.dto.*
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.repository.LessonHistoryCommentRepository
import com.tobe.healthy.lessonHistory.repository.LessonHistoryRepository
import com.tobe.healthy.log
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.repository.ScheduleRepository
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

    fun registerLessonHistory(request: RegisterLessonHistoryCommand, uploadFiles: MutableList<MultipartFile>, studentId: Long): Boolean {
        val findMember = memberRepository.findByIdOrNull(studentId) ?: throw CustomException(MEMBER_NOT_FOUND)

        val findTrainer = memberRepository.findByIdOrNull(request.trainer) ?: throw CustomException(TRAINER_NOT_FOUND)

        val findSchedule = scheduleRepository.findByIdOrNull(request.schedule) ?: throw CustomException(SCHEDULE_NOT_FOUND)

        val lessonHistory = LessonHistory.register(request, findMember, findTrainer, findSchedule)

        lessonHistoryRepository.save(lessonHistory)

        var fileOrder = 1;
        for (uploadFile in uploadFiles) {
            uploadFile.let {
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
                    fileOrder++
                )
                awsS3FileRepository.save(file)
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

    fun findAllLessonHistory(request: SearchCondRequest, memberId: Long): List<LessonHistoryCommandResult> {
        val findMember = memberRepository.findByIdOrNull(memberId) ?: throw CustomException(MEMBER_NOT_FOUND)
        return lessonHistoryRepository.findAllLessonHistory(request, findMember.id, findMember.memberType)
    }

    fun findOneLessonHistory(lessonHistoryId: Long, memberId: Long): List<LessonHistoryCommandResult> {
        val findMember = memberRepository.findByIdOrNull(memberId) ?: throw CustomException(MEMBER_NOT_FOUND)
        return lessonHistoryRepository.findOneLessonHistory(lessonHistoryId, findMember.id, findMember.memberType)
    }

    fun updateLessonHistory(lessonHistoryId: Long, request: LessonHistoryUpdateCommand): Boolean {
        val findEntity = lessonHistoryRepository.findByIdOrNull(lessonHistoryId) ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
        findEntity.updateLessonHistory(request.title, request.content)
        return true
    }

    fun updateLessonHistoryComment(lessonHistoryCommentId: Long, request: LessonHistoryCommentUpdateCommand): Boolean {
        val findEntity = lessonHistoryCommentRepository.findByIdOrNull(lessonHistoryCommentId) ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
        findEntity.updateLessonHistoryComment(request.content)
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
}
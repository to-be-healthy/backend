package com.tobe.healthy.lessonHistory.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.file.domain.entity.Profile
import com.tobe.healthy.file.repository.FileRepository
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommentUpdateCommand
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryUpdateCommand
import com.tobe.healthy.lessonHistory.domain.dto.RegisterLessonHistoryCommand
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.repository.LessonHistoryCommentRepository
import com.tobe.healthy.lessonHistory.repository.LessonHistoryRepository
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.repository.ScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
@Transactional
class LessonHistoryService(
    private val lessonHistoryRepository: LessonHistoryRepository,
    private val memberRepository: MemberRepository,
    private val scheduleRepository: ScheduleRepository,
    private val lessonHistoryCommentRepository: LessonHistoryCommentRepository,
    private val fileRepository: FileRepository
) {

    fun registerLessonHistory(request: RegisterLessonHistoryCommand, uploadFile: MultipartFile, studentId: Long): Boolean {
        val findMember = memberRepository.findById(studentId).orElseThrow {
            throw CustomException(MEMBER_NOT_FOUND)
        }

        val findTrainer = memberRepository.findById(request.trainer).orElseThrow {
            throw CustomException(TRAINER_NOT_FOUND)
        }

        val findSchedule = scheduleRepository.findById(request.schedule).orElseThrow {
            throw CustomException(SCHEDULE_NOT_FOUND)
        }

        val lessonHistory = LessonHistory.register(request, findMember, findTrainer, findSchedule)

        lessonHistoryRepository.save(lessonHistory)

        val uploadDir = "upload"

        uploadFile.let {
            val savedFileName = System.currentTimeMillis().toString() + "_" + UUID.randomUUID()
            val extension = Objects.requireNonNull(uploadFile.originalFilename)?.substring(
                uploadFile.originalFilename!!.lastIndexOf(".")
            )

            val copyOfLocation = Paths.get(uploadDir + File.separator + StringUtils.cleanPath(savedFileName + extension))
            Files.copy(uploadFile.inputStream, copyOfLocation, StandardCopyOption.REPLACE_EXISTING)

            val profile = Profile.create(
                savedFileName,
                StringUtils.cleanPath(uploadFile.originalFilename!!),
                extension,
                uploadDir + File.separator,
                uploadFile.size.toInt(),
                lessonHistory
            )

            findMember.registerProfile(profile)

            fileRepository.save(profile)
        }

        return true
    }

    fun findAllLessonHistory(): List<LessonHistoryCommandResult> {
        return lessonHistoryRepository.findAllLessonHistory()
    }

    fun findOneLessonHistory(lessonHistoryId: Long): LessonHistoryCommandResult {
        val result = lessonHistoryRepository.findById(lessonHistoryId).orElseThrow {
            throw CustomException(LESSON_HISTORY_NOT_FOUND)
        }
        return lessonHistoryRepository.findOneLessonHistory(result.id!!)
    }

    fun updateLessonHistory(lessonHistoryId: Long, request: LessonHistoryUpdateCommand): Boolean {
        val findEntity = lessonHistoryRepository.findById(lessonHistoryId).orElseThrow {
            throw CustomException(LESSON_HISTORY_NOT_FOUND)
        }
        findEntity.updateLessonHistory(request.title, request.content)
        return true
    }

    fun updateLessonHistoryComment(lessonHistoryCommentId: Long, request: LessonHistoryCommentUpdateCommand): Boolean {
        val findEntity = lessonHistoryCommentRepository.findById(lessonHistoryCommentId)
            .orElseThrow {
                throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)
            }
        findEntity.updateLessonHistoryComment(request.content)
        return true
    }

    fun deleteLessonHistory(lessonHistoryId: Long): Boolean {
        lessonHistoryRepository.deleteById(lessonHistoryId)
        return true
    }

    fun deleteLessonHistoryComment(lessonHistoryCommentId: Long): Boolean {
        lessonHistoryCommentRepository.deleteById(lessonHistoryCommentId)
        return true
    }
}
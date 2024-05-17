package com.tobe.healthy.lessonhistory.application

import com.tobe.healthy.common.redis.RedisKeyPrefix.TEMP_FILE_URI
import com.tobe.healthy.common.redis.RedisService
import com.tobe.healthy.lessonhistory.domain.dto.`in`.CommentRegisterCommand
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RegisterLessonHistoryCommand
import com.tobe.healthy.lessonhistory.domain.dto.out.RegisterLessonHistoryCommandResponse
import com.tobe.healthy.lessonhistory.domain.dto.out.RegisterLessonHistoryCommentResponse
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Transactional
@SpringBootTest
class LessonHistoryServiceTest(
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val lessonHistoryService: LessonHistoryService,
    private val redisService: RedisService
) : StringSpec({

    lateinit var student: Member
    lateinit var trainer: Member
    lateinit var schedule: Schedule

    beforeTest {
        student = memberRepository.findByUserId("healthy-student0").get()
        trainer = memberRepository.findByUserId("healthy-trainer0").get()
        schedule = trainerScheduleRepository.findByIdOrNull(1L)!!
    }

    fun createLessonHistory(
        studentId: Long,
        trainerId: Long,
        scheduleId: Long,
        title: String,
        content: String
    ): RegisterLessonHistoryCommandResponse {
        return RegisterLessonHistoryCommand(
            title = title,
            content = content,
            studentId = studentId,
            scheduleId = scheduleId,
            null
        ).let { lessonHistoryService.registerLessonHistory(it, trainerId) }
    }

    "수업일지를 작성한다" {
        val request = RegisterLessonHistoryCommand(
            title = "수업일지 테스트 제목",
            content = "수업일지 테스트 내용",
            studentId = student.id,
            scheduleId = schedule.id,
            null
        )

        val result = lessonHistoryService.registerLessonHistory(request, trainer.id)

        result.title shouldBe request.title
        result.content shouldBe request.content
    }

    "첨부파일을 등록하고 레디스에 저장이 돼 있는지 검증한다" {
        val files = mutableListOf<MultipartFile>(
            MockMultipartFile("file", "file1.txt", "text/plain", "some content".toByteArray()),
            MockMultipartFile("file", "file1.txt", "text/plain", "some content".toByteArray()),
            MockMultipartFile("file", "file1.txt", "text/plain", "some content".toByteArray())
        )

        val results = lessonHistoryService.registerFilesOfLessonHistory(files, trainer.id)

        for (result in results) {
            val value = redisService.getValues(TEMP_FILE_URI.description + result.fileUrl)
            value shouldBe trainer.id.toString()
        }
        results.size shouldBe 3
    }

    "수업일지에 댓글을 작성한다" {
        val result = createLessonHistory(student.id, trainer.id, schedule.id, "수업일지 제목", "수업일지 내용")

        val commentRequest = CommentRegisterCommand(comment = "수업일지 테스트 댓글", null)
        val response =
            lessonHistoryService.registerLessonHistoryComment(result.lessonHistoryId, commentRequest, student.id)

        response.comment shouldBe commentRequest.comment
        response.writerId shouldBe student.id
    }

    fun createLessonHistoryComment(
        comment: String,
        lessonHistoryId: Long,
        memberId: Long
    ): RegisterLessonHistoryCommentResponse {
        return CommentRegisterCommand(comment = comment, null).let {
            lessonHistoryService.registerLessonHistoryComment(lessonHistoryId, it, memberId)
        }
    }

    "수업일지에 대댓글을 작성한다" {
        // given
        val result = createLessonHistory(student.id, trainer.id, schedule.id, "수업일지 제목", "수업일지 내용")
        val response = createLessonHistoryComment("수업일지 테스트 댓글1", result.lessonHistoryId, student.id)
        val resultReply = lessonHistoryService.registerLessonHistoryReply(
            result.lessonHistoryId,
            response.lessonHistoryCommentId,
            CommentRegisterCommand("대댓글!", null),
            trainer.id
        )

        resultReply.content shouldBe "대댓글!"
    }
})
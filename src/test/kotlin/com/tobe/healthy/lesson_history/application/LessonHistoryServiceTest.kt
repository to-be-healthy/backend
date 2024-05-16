package com.tobe.healthy.lesson_history.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.LESSON_HISTORY_COMMENT_NOT_FOUND
import com.tobe.healthy.config.error.ErrorCode.LESSON_HISTORY_NOT_FOUND
import com.tobe.healthy.lesson_history.domain.entity.LessonHistory
import com.tobe.healthy.lesson_history.domain.entity.LessonHistoryComment
import com.tobe.healthy.lesson_history.repository.LessonHistoryCommentRepository
import com.tobe.healthy.lesson_history.repository.LessonHistoryRepository
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.Schedule
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Transactional
@SpringBootTest
class LessonHistoryServiceTest(
    private val em: EntityManager,
    private val passwordEncoder: PasswordEncoder,
    private val lessonHistoryRepository: LessonHistoryRepository,
    private val lessonHistoryCommentRepository: LessonHistoryCommentRepository,
    private val memberRepository: MemberRepository
) : BehaviorSpec({

    Given("회원가입을 하고") {
        val student = memberRepository.findByUserId("healthy-student0").get()
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()
        When("게시글과 댓글을 등록했을 때") {
            val schedule = createSchedule(trainer, student)
            val lessonHistory = createLessonHistory("테스트 게시글 제목", "테스트 게시글 내용", trainer, student, schedule)
            em.persist(schedule)
            em.persist(lessonHistory)
            val comment = createLessonHistoryComment(lessonHistory)
            em.persist(comment)
            em.flush()
            em.clear()

            Then("게시글이 정상적으로 등록되었는지 검증한다") {
                val result = lessonHistoryRepository.findByIdOrNull(lessonHistory.id) ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
                result.content shouldBe lessonHistory.content
            }

            Then("댓글이 정상적으로 등록되었는지 검증한다") {
                val result = lessonHistoryCommentRepository.findByIdOrNull(comment.id) ?: throw CustomException(LESSON_HISTORY_COMMENT_NOT_FOUND)
                result.content = comment.content
            }
        }
    }
})
{
    companion object {
        private fun createLessonHistoryComment(lessonHistory: LessonHistory): LessonHistoryComment {
        return LessonHistoryComment(
            order = 1,
            content = "테스트용 댓글 등록",
            writer = lessonHistory.student,
            lessonHistory = lessonHistory)
        }

        private fun createLessonHistory(title: String, content: String, trainer: Member, student: Member, schedule: Schedule) : LessonHistory {
            return LessonHistory(
                title,
                content,
                mutableListOf(),
                mutableListOf(),
                trainer,
                student,
                schedule
            )
        }

        private fun createSchedule(trainer: Member, student: Member): Schedule {
        return Schedule.builder()
            .lessonDt(LocalDate.of(2024, 4, 1))
            .lessonStartTime(LocalTime.of(10, 0))
            .lessonEndTime(LocalTime.of(11, 0))
            .reservationStatus(COMPLETED)
            .trainer(trainer)
            .applicant(student)
            .build()
        }
    }
}

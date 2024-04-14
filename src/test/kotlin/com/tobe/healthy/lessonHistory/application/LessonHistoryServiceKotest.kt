package com.tobe.healthy.lessonHistory.application

import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonHistory.repository.LessonHistoryCommentRepository
import com.tobe.healthy.lessonHistory.repository.LessonHistoryRepository
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType.STUDENT
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.Schedule
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Transactional
@SpringBootTest
class LessonHistoryServiceKotest(
    private val em: EntityManager,
    private val passwordEncoder: PasswordEncoder,
    private val queryFactory: JPAQueryFactory,
    private val lessonHistoryRepository: LessonHistoryRepository,
    private val lessonHistoryCommentRepository: LessonHistoryCommentRepository,
) : StringSpec({
    extensions(SpringExtension)

    "수업 내역을 생성하고 게시글 내용이 같은지 검증한다" {
        val student = createStudent(passwordEncoder.encode("pass123"))
        val trainer = createTrainer(passwordEncoder.encode("pass123"))
        val schedule = createSchedule(trainer, student)
        val lessonHistory = createLessonHistory("테스트 게시글 제목", "테스트 게시글 내용", trainer, student, schedule)
        em.persist(student)
        em.persist(trainer)
        em.persist(schedule)
        em.persist(lessonHistory)
        lessonHistory.content shouldBe "테스트 게시글 내용"
    }

    "수업 내역에 댓글을 작성한다" {
        val student = createStudent(passwordEncoder.encode("pass123"))
        val trainer = createTrainer(passwordEncoder.encode("pass123"))
        val schedule = createSchedule(trainer, student)
        val lessonHistory = createLessonHistory("테스트 게시글 제목", "테스트 게시글 내용", trainer, student, schedule)
//        createLessonHistoryComment()
        em.persist(student)
        em.persist(trainer)
        em.persist(schedule)
        em.persist(lessonHistory)
        lessonHistory.content shouldBe "테스트 게시글 내용"
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
            .round(1)
            .trainer(trainer)
            .applicant(student)
            .build()
        }

        private fun createStudent(password: String): Member {
            return Member.builder()
                .userId("student")
                .email("student@gmail.com")
                .password(password)
                .name("student")
                .memberType(STUDENT)
                .build()
        }

        private fun createTrainer(password: String): Member {
            return Member.builder()
                .userId("trainer")
                .email("trainer@gmail.com")
                .password(password)
                .name("trainer")
                .memberType(TRAINER)
                .build()
        }
    }
}




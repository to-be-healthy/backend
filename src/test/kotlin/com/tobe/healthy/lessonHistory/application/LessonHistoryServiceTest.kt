package com.tobe.healthy.lessonHistory.application

import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.lessonHistory.domain.QLessonHistory.lessonHistory
import com.tobe.healthy.lessonHistory.domain.QLessonHistoryComment.lessonHistoryComment
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType.STUDENT
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.Schedule
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
class LessonHistoryServiceTest @Autowired constructor(
    private val em: EntityManager,
    private val passwordEncoder: PasswordEncoder,
    private val queryFactory: JPAQueryFactory
) {

    @Test
    fun `게시글을 등록한다`() {
        val student = Member.builder()
            .userId("student")
            .email("student@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("student")
            .memberType(STUDENT)
            .build()

        val trainer = Member.builder()
            .userId("trainer")
            .email("trainer@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("trainer")
            .memberType(TRAINER)
            .build()

        em.persist(student)
        em.persist(trainer)

        val schedule = Schedule.builder()
            .lessonDt(LocalDate.of(2024, 4, 1))
            .lessonStartTime(LocalTime.of(10, 0))
            .lessonEndTime(LocalTime.of(11, 0))
            .reservationStatus(COMPLETED)
            .round(1)
            .trainer(trainer)
            .applicant(student)
            .build()

        em.persist(schedule)

        val lessonHistory = LessonHistory(
            "첫 수업 후기입니다.",
            "전체적으로 배우는게 빨라서 금방 몸짱 되시겠네요!",
            null,
            null,
            trainer,
            student,
            schedule)

        em.persist(lessonHistory)
    }

    @Test
    @Rollback(value = false)
    fun `게시글에 댓글을 등록한다`() {
        val student = Member.builder()
            .userId("student")
            .email("student@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("student")
            .memberType(STUDENT)
            .build()

        val trainer = Member.builder()
            .userId("trainer")
            .email("trainer@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("trainer")
            .memberType(TRAINER)
            .build()

        em.persist(student)
        em.persist(trainer)

        val schedule = Schedule.builder()
            .lessonDt(LocalDate.of(2024, 4, 1))
            .lessonStartTime(LocalTime.of(10, 0))
            .lessonEndTime(LocalTime.of(11, 0))
            .reservationStatus(COMPLETED)
            .round(1)
            .trainer(trainer)
            .applicant(student)
            .build()

        em.persist(schedule)

        val lessonHistory = LessonHistory(
            title = "첫 수업 후기입니다.",
            content = "전체적으로 배우는게 빨라서 금방 몸짱 되시겠네요!",
            trainer = trainer,
            student = student,
            schedule = schedule
        )

        val lessonHistoryComment1 = LessonHistoryComment(
            order = 1,
            content = "선생님 덕분에 빨리 배우는 것 같아요 감사합니다!",
            writer = student,
            lessonHistory = lessonHistory
        )

        val lessonHistoryComment2 = LessonHistoryComment(
            order = 2,
            content = "네!! 화이팅입니다 ^^",
            writer = trainer,
            lessonHistory = lessonHistory
        )
        em.persist(lessonHistory)
        em.persist(lessonHistoryComment1)
        em.persist(lessonHistoryComment2)

        val lessonHistoryComment3 = LessonHistoryComment(
            parentId = lessonHistoryComment2,
            order = 3,
            content = "네네!! 잘 부탁드려요^^",
            writer = student,
            lessonHistory = lessonHistory
        )

        em.persist(lessonHistoryComment3)
    }

    @Test
    fun `게시글을 조회한다`() {
        val lessonHistory = queryFactory
            .select(lessonHistory)
            .from(lessonHistory)
            .fetch()

        val lessonHistoryComment = queryFactory
            .select(lessonHistoryComment)
            .from(lessonHistoryComment)
            .fetch()

        // 모든 댓글을 먼저 parentId가 null인 것과 아닌 것으로 분류
        val (parentComments, childComments) = lessonHistoryComment?.partition { it.parentId == null } ?: Pair(listOf(), listOf())

        // 부모 댓글을 order로 정렬
        val sortedParentComments = parentComments.sortedBy { it.order }

        // 대댓글을 parentId의 id와 order로 정렬
        val sortedChildComments = childComments.groupBy { it.parentId?.id }
            .mapValues { (_, value) -> value.sortedBy { it.order } }

        // 댓글 출력
        sortedParentComments.forEach { parent ->
            println("Parent Comment ID: ${parent.id}, Content: ${parent.content}")

            // 해당 부모 댓글에 대한 대댓글 출력
            sortedChildComments[parent.id]?.forEach { child ->
                println("\tChild Comment ID: ${child.id}, Content: ${child.content}, Order: ${child.order}")
            }
        }
    }
}
package com.tobe.healthy.lessonHistory.application

import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonHistory.domain.entity.QLessonHistory
import com.tobe.healthy.lessonHistory.repository.LessonHistoryCommentRepository
import com.tobe.healthy.log
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberType.STUDENT
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.Schedule
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.util.stream.Collectors

@SpringBootTest
@Transactional
class LessonHistoryServiceTest @Autowired constructor(
    private val em: EntityManager,
    private val passwordEncoder: PasswordEncoder,
    private val queryFactory: JPAQueryFactory,
    private val lessonHistoryCommandRepository: LessonHistoryCommentRepository
) {

    lateinit var student: Member
    lateinit var trainer: Member
    lateinit var schedule: Schedule
    lateinit var lessonHistory: LessonHistory

    @BeforeEach
    fun `초기 데이터를 설정한다`() {
        student = Member.builder()
            .userId("trainer")
            .email("trainer@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("trainer")
            .memberType(TRAINER)
            .build()

        trainer = Member.builder()
            .userId("student")
            .email("student@gmail.com")
            .password(passwordEncoder.encode("zxcvbnm11"))
            .name("student")
            .memberType(STUDENT)
            .build()

        schedule = Schedule.builder()
            .lessonDt(LocalDate.of(2024, 4, 1))
            .lessonStartTime(LocalTime.of(10, 0))
            .lessonEndTime(LocalTime.of(11, 0))
            .reservationStatus(COMPLETED)
            .round(1)
            .trainer(trainer)
            .applicant(student)
            .build()

        lessonHistory = LessonHistory(
            "title",
            "content",
            null,
            null,
            trainer,
            student,
            schedule)

        em.persist(student)
        em.persist(trainer)
        em.persist(schedule)
        em.persist(lessonHistory)
    }

    @Test
    fun `게시글에 댓글을 등록한다`() {
        val order = lessonHistoryCommandRepository.findTopComment(lessonHistory.id!!) ?: 1
        val lessonHistoryComment = getLessonHistoryComment(order, "123213", student)
        em.persist(lessonHistoryComment)
    }

    @Test
    fun `게시글에 댓글을 두개 등록한다`() {
        val order = lessonHistoryCommandRepository.findTopComment(lessonHistory.id!!) ?: 1
        val lessonHistoryComment = getLessonHistoryComment(order, "댓글 내용입니다!!", student)
        em.persist(lessonHistoryComment)

        val order2 = lessonHistoryCommandRepository.findTopComment(lessonHistory.id!!) ?: 1
        val lessonHistoryComment2 = getLessonHistoryComment(order2, "댓글 내용입니다!!(2)", trainer)
        em.persist(lessonHistoryComment2)
    }

    @Test
    @Rollback(false)
    fun `게시글에 대댓글을 등록한다`() {
        val order = lessonHistoryCommandRepository.findTopComment(lessonHistory.id!!) ?: 1
        val lessonHistoryComment = getLessonHistoryComment(order, "댓글 내용입니다!!", student)
        em.persist(lessonHistoryComment)

        val order2 = lessonHistoryCommandRepository.findTopComment(lessonHistory.id!!, lessonHistoryComment.id!!) ?: 1
        val lessonHistoryComment2 = getLessonHistoryComment(order2, "대댓글 작성!!", trainer, lessonHistoryComment)
        em.persist(lessonHistoryComment2)

        val order3 = lessonHistoryCommandRepository.findTopComment(lessonHistory.id!!, lessonHistoryComment.id!!) ?: 1
        val lessonHistoryComment3 = getLessonHistoryComment(order3, "대댓글 작성 두번 째!!", student, lessonHistoryComment)
        em.persist(lessonHistoryComment3)
    }

    @Test
    fun `게시글을 조회한다`() {
        val lessonHistory = queryFactory
            .selectDistinct(QLessonHistory.lessonHistory)
            .from(QLessonHistory.lessonHistory)
            .leftJoin(QLessonHistory.lessonHistory.lessonHistoryComment).fetchJoin()
            .fetch()

        val results = lessonHistory.stream().map { entity -> LessonHistoryCommandResult.from(entity) }.collect(Collectors.toList())

        for (result in results) {
            log.info { "result => ${result}" }
        }
    }

    private fun getLessonHistoryComment(order: Int, content: String, writer: Member, parentId: LessonHistoryComment? = null) =
        LessonHistoryComment(
        order = order,
        content = content,
        writer = writer,
        lessonHistory = lessonHistory,
        parentId = parentId
        )
}

package com.tobe.healthy.lessonHistory.application

import com.tobe.healthy.lessonHistory.domain.LessonHistory
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
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
class LessonHistoryServiceTest @Autowired constructor(
    private val em: EntityManager,
    private val passwordEncoder: PasswordEncoder
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

        val lessonHistory = LessonHistory("첫 수업 후기입니다.", "전체적으로 배우는게 빨라서~~~", null, trainer, student, schedule)

        em.persist(lessonHistory)
    }
}
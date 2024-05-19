package com.tobe.healthy.schedule.application

import com.tobe.healthy.log
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@SpringBootTest
@Transactional
class ScheduleAutoDeleteTest(
    private val em: EntityManager,
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository
) : StringSpec({

    lateinit var trainer: Member

    beforeTest {
        trainer = memberRepository.findByUserId("healthy-trainer0").get()
    }

    "지난 주를 조회한다" {
        // 매주 월요일날 실행해야함
        val today = LocalDate.of(2024, 5, 20) // 월요일
        val startOfLastWeek = today.with(TemporalAdjusters.previous(MONDAY))
        val endOfLastWeek = startOfLastWeek.with(TemporalAdjusters.nextOrSame(SUNDAY))
        log.info { "startOfLastWeek: $startOfLastWeek, endOfLastWeek: $endOfLastWeek" }
    }

    "지난 주 비활성화 된 일정을 삭제한다" {
        val today = LocalDate.of(2024, 5, 20) // 월요일
        val startOfLastWeek = today.with(TemporalAdjusters.previous(MONDAY))
        val endOfLastWeek = startOfLastWeek.with(TemporalAdjusters.nextOrSame(SUNDAY))
        val schedules = trainerScheduleRepository.findAllDisabledSchedule(startOfLastWeek, endOfLastWeek, trainer.id)

        trainerScheduleRepository.deleteAll(schedules)

        em.flush()
        em.clear()
        val deleteSchedules = trainerScheduleRepository.findAllDisabledSchedule(startOfLastWeek, endOfLastWeek, trainer.id)

        deleteSchedules.size shouldBe 0
    }
})
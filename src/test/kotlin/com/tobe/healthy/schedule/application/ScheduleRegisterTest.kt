package com.tobe.healthy.schedule.application

import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterSchedule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
class ScheduleRegisterTest(
    private val trainerScheduleCommandService: TrainerScheduleCommandService,
    private val memberRepository: MemberRepository,
) : StringSpec({

    "30일간의 일정을 등록한다" {
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()

        val request = CommandRegisterSchedule(
            lessonStartDt = LocalDate.of(2024, 6, 1),
            lessonEndDt = LocalDate.of(2024, 6, 30),
        )
        val result = trainerScheduleCommandService.registerSchedule(request, trainer.id)

        result.lessonDt.size shouldBe 30
        result.lessonStartTime shouldBe LocalTime.of(9, 0, 0)
        result.lessonEndTime shouldBe LocalTime.of(20, 0, 0)
    }
})

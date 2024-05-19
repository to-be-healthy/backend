package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_ALREADY_EXISTS
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.entity.`in`.CommandRegisterIndividualSchedule
import com.tobe.healthy.schedule.entity.`in`.CommandRegisterSchedule
import io.kotest.assertions.throwables.shouldThrow
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
            lessonStartDt = LocalDate.of(2024, 5, 1),
            lessonEndDt = LocalDate.of(2024, 5, 30),
        )
        val result = trainerScheduleCommandService.registerSchedule(request, trainer.id)

        result.lessonDt.size shouldBe 30
        result.lessonStartTime shouldBe LocalTime.of(9, 0, 0)
        result.lessonEndTime shouldBe LocalTime.of(20, 0, 0)
    }

    "이미 등록된 일정이 있으면 예외를 발생시킨다" {
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()

        val request = CommandRegisterIndividualSchedule(
            lessonDt = LocalDate.of(2024, 5, 31),
            lessonStartTime = LocalTime.of(19, 0, 0),
            lessonEndTime = LocalTime.of(20, 0, 0),
        )

        val message = shouldThrow<CustomException> {
            trainerScheduleCommandService.registerIndividualSchedule(request, trainer.id)
        }.message

        message shouldBe SCHEDULE_ALREADY_EXISTS.message
    }

    "일정을 개별 등록하고 검증한다" {
        val trainer = memberRepository.findByUserId("healthy-trainer0").get()
        val singleDayRequest = CommandRegisterIndividualSchedule(
            lessonDt = LocalDate.of(2024, 5, 1),
            lessonStartTime = LocalTime.of(9, 0, 0),
            lessonEndTime = LocalTime.of(10, 0, 0)
        )
        val result = trainerScheduleCommandService.registerIndividualSchedule(singleDayRequest, trainer.id)
        result shouldBe true
    }
})

package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
class ScheduleSettingTest(

) : StringSpec({

    "수업 시작 일자가 종료 일자보다 같거나 빠르면 정상적으로 등록된다" {

        val registerRequest1 = RegisterScheduleRequest(
            lessonStartDt = LocalDate.of(2024, 5, 30),
            lessonEndDt = LocalDate.of(2024, 5, 30),
        )

        val registerRequest2 = RegisterScheduleRequest(
            lessonStartDt = LocalDate.of(2024, 5, 29),
            lessonEndDt = LocalDate.of(2024, 5, 30),
        )

        registerRequest1.lessonStartDt shouldBe LocalDate.of(2024, 5, 30)
        registerRequest1.lessonEndDt shouldBe LocalDate.of(2024, 5, 30)

        registerRequest2.lessonStartDt shouldBe LocalDate.of(2024, 5, 29)
        registerRequest2.lessonEndDt shouldBe LocalDate.of(2024, 5, 30)
    }

    "수업 시작 일자가 종료 일자보다 미래면 예외가 발생한다" {
        val message = shouldThrow<CustomException> {
            RegisterScheduleRequest(
                lessonStartDt = LocalDate.of(2024, 6, 1),
                lessonEndDt = LocalDate.of(2024, 5, 30),
            )
        }.message

        message shouldBe START_DATE_AFTER_END_DATE.message
    }

    "수업 시작 시간보다 종료 시간이 같거나 빠르면 예외가 발생한다" {
        shouldThrow<CustomException> {
            RegisterScheduleCommand(
                lessonDt = LocalDate.of(2024, 5, 1),
                lessonStartTime = LocalTime.of(12, 0, 0),
                lessonEndTime = LocalTime.of(10, 0, 0)
            )
        }.message shouldBe START_TIME_AFTER_END_TIME.message

        shouldThrow<CustomException> {
            RegisterScheduleCommand(
                lessonDt = LocalDate.of(2024, 5, 1),
                lessonStartTime = LocalTime.of(10, 0, 0),
                lessonEndTime = LocalTime.of(10, 0, 0)
            )
        }.message shouldBe START_TIME_AFTER_END_TIME.message
    }

    "수업 시간이 30분 단위가 아닐경우 예외가 발생한다" {
        shouldThrow<CustomException> {
            RegisterScheduleCommand(
                lessonDt = LocalDate.of(2024, 5, 1),
                lessonStartTime = LocalTime.of(10, 0, 0),
                lessonEndTime = LocalTime.of(11, 10, 0)
            )
        }.message shouldBe INVALID_LESSON_TIME_DESCRIPTION.message


        val request = RegisterScheduleCommand(
            lessonDt = LocalDate.of(2024, 5, 1),
            lessonStartTime = LocalTime.of(10, 0, 0),
            lessonEndTime = LocalTime.of(11, 0, 0)
        )

        request.lessonDt shouldBe LocalDate.of(2024, 5, 1)
        request.lessonStartTime shouldBe LocalTime.of(10, 0, 0)
        request.lessonEndTime shouldBe LocalTime.of(11, 0, 0)
    }
})

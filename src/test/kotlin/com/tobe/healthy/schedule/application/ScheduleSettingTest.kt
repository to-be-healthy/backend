package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.START_DATE_AFTER_END_DATE
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterSchedule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@Transactional
class ScheduleSettingTest : StringSpec({

    "수업 시작 일자가 종료 일자보다 같거나 빠르면 정상적으로 등록된다" {

        val registerRequest1 = CommandRegisterSchedule(
            lessonStartDt = LocalDate.of(2024, 5, 30),
            lessonEndDt = LocalDate.of(2024, 5, 30),
        )

        val registerRequest2 = CommandRegisterSchedule(
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
            CommandRegisterSchedule(
                lessonStartDt = LocalDate.of(2024, 6, 1),
                lessonEndDt = LocalDate.of(2024, 5, 30),
            )
        }.message

        message shouldBe START_DATE_AFTER_END_DATE.message
    }
})

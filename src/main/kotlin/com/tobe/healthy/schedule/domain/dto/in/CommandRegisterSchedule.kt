package com.tobe.healthy.schedule.domain.dto.`in`

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_LESS_THAN_31_DAYS
import com.tobe.healthy.config.error.ErrorCode.START_DATE_AFTER_END_DATE
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class CommandRegisterSchedule(
    @Schema(description = "시작 수업 일자", example = "2024-04-01")
    @field:NotNull(message = "시작 수업 일자를 입력해 주세요.")
    val lessonStartDt: LocalDate?,

    @Schema(description = "종료 수업 일자", example = "2024-04-30")
    @field:NotNull(message = "종료 수업 일자를 입력해 주세요.")
    val lessonEndDt: LocalDate?,
) {
    init {
        if (lessonStartDt!!.isAfter(lessonEndDt)) {
            throw CustomException(START_DATE_AFTER_END_DATE)
        }
        if (ChronoUnit.DAYS.between(lessonStartDt, lessonEndDt) > 31) {
            throw CustomException(SCHEDULE_LESS_THAN_31_DAYS)
        }
    }
}

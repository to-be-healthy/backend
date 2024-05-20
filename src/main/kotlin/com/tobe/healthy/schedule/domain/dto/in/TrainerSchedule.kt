package com.tobe.healthy.schedule.domain.dto.`in`

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.SEARCH_LESS_THAN_31_DAYS
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class TrainerSchedule(
    @Schema(description = "조회할 수업 일자", example = "2024-04")
    var lessonDt: String? = null,

    @Schema(description = "조회할 수업 시작 일자", example = "2024-04-01")
    val lessonStartDt: LocalDate? = null,

    @Schema(description = "조회할 수업 종료 일자", example = "2024-04-30")
    val lessonEndDt: LocalDate? = null
) {
    init {
        if (lessonDt == null && lessonStartDt == null && lessonEndDt == null) {
            lessonDt = DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now()).toString()
        }

        if (lessonStartDt != null && lessonEndDt != null && ChronoUnit.DAYS.between(lessonStartDt, lessonEndDt) > 31) {
            throw CustomException(SEARCH_LESS_THAN_31_DAYS)
        }
    }
}

package com.tobe.healthy.schedule.domain.dto.`in`

import com.tobe.healthy.common.error.CustomException
import com.tobe.healthy.common.error.ErrorCode.SEARCH_LESS_THAN_31_DAYS
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class RetrieveTrainerScheduleByLessonInfo(
    @Schema(description = "조회할 수업 일자", example = "2024-04")
    var lessonDt: String? = null,

    @Schema(description = "조회할 수업 시작 일자", example = "2024-04-01")
    val lessonStartDt: LocalDate? = null,

    @Schema(description = "조회할 수업 종료 일자", example = "2024-04-30")
    val lessonEndDt: LocalDate? = null
) {
    init {
        if (lessonDt == null && lessonStartDt == null && lessonEndDt == null) {
            lessonDt = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        }

        if (lessonStartDt != null && lessonEndDt != null && ChronoUnit.DAYS.between(lessonStartDt, lessonEndDt) > 31) {
            throw CustomException(SEARCH_LESS_THAN_31_DAYS)
        }
    }
}

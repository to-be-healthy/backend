package com.tobe.healthy.schedule.entity.`in`

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class ScheduleSearchCond(
    @Schema(description = "조회할 수업 일자", example = "202404")
    val lessonDt: String? = null,

    @Schema(description = "조회할 수업 시작 일자", example = "2024-04-01")
    val lessonStartDt: LocalDate? = null,

    @Schema(description = "조회할 수업 종료 일자", example = "2024-04-30")
    val lessonEndDt: LocalDate? = null
)

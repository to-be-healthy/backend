package com.tobe.healthy.schedule.entity.`in`

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class RegisterScheduleRequest(
    @Schema(description = "시작 수업 일자", example = "2024-04-01")
    val lessonStartDt: LocalDate,

    @Schema(description = "종료 수업 일자", example = "2024-04-30")
    val lessonEndDt: LocalDate,
)

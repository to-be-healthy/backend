package com.tobe.healthy.schedule.entity.`in`

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

data class RegisterScheduleCommand(
    @Schema(description = "등록할 수업 일자", example = "2024-04-01")
    val lessonDt: LocalDate,

    @Schema(description = "등록할 수업 시작 시간", example = "10:00:00", type = "string")
    val lessonStartTime: LocalTime,

    @Schema(description = "등록할 수업 종료 시간", example = "11:00:00", type = "string")
    val lessonEndTime: LocalTime
)

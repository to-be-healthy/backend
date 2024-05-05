package com.tobe.healthy.schedule.entity.`in`

import com.tobe.healthy.schedule.domain.entity.LessonTime
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

data class RegisterScheduleRequest(
    @Schema(description = "시작 수업 일자", example = "2024-04-01")
    val startDt: LocalDate,

    @Schema(description = "종료 수업 일자", example = "2024-04-30")
    val endDt: LocalDate,

    @Schema(description = "시작 수업 시간", example = "10:00:00", type = "string")
    val startTime: LocalTime,

    @Schema(description = "종료 수업 시간", example = "22:00:00", type = "string")
    val endTime: LocalTime,

    @Schema(description = "시작 점심시간", example = "12:00:00", type = "string")
    val lunchStartTime: LocalTime? = null,

    @Schema(description = "종료 점심시간", example = "13:00:00", type = "string")
    val lunchEndTime: LocalTime? = null,

    @Schema(description = "휴무일", example = "2024-04-05", type = "string")
    val closedDt: List<LocalDate>? = null,

    @Schema(description = "세션당 수업 시간", example = "30 | 60 | 90 | 120")
    val sessionTime: LessonTime
)

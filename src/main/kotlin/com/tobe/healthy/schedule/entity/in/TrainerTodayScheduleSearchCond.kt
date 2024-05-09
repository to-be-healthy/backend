package com.tobe.healthy.schedule.entity.`in`

import io.swagger.v3.oas.annotations.media.Schema

data class TrainerTodayScheduleSearchCond(
    @Schema(description = "조회할 수업 일자", example = "2024-04-01")
    val lessonDt: String,
)

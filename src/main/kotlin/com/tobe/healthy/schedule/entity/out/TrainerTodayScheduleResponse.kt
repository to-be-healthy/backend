package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.schedule.entity.out.LessonResponse.LessonDetailResponse

data class TrainerTodayScheduleResponse(
    val trainerName: String? = null,
    val scheduleTotalCount: Long,
    val before: MutableList<LessonDetailResponse?> = mutableListOf(),
    val after: MutableList<LessonDetailResponse?> = mutableListOf()
)

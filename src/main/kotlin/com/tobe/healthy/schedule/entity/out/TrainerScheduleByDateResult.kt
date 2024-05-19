package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.schedule.entity.out.TrainerScheduleResult.LessonDetailResult

data class TrainerScheduleByDateResult(
    val trainerName: String? = null,
    val scheduleTotalCount: Long,
    val before: MutableList<LessonDetailResult?> = mutableListOf(),
    val after: MutableList<LessonDetailResult?> = mutableListOf()
)

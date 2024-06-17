package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonInfoResult.LessonDetailResult

data class RetrieveTrainerScheduleByLessonDtResult(
    val trainerName: String? = null,
    val scheduleTotalCount: Long,
    val schedule: MutableList<LessonDetailResult?> = mutableListOf()
)

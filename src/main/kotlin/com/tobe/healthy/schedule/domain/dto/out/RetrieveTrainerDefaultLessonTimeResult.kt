package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.common.TimeFormatter.Companion.dateTimeFormat
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo
import java.time.DayOfWeek

data class RetrieveTrainerDefaultLessonTimeResult(
    val lessonStartTime: String?,
    val lessonEndTime: String?,
    val lunchStartTime: String? = null,
    val lunchEndTime: String? = null,
    val lessonTime: Int?,
    val closedDays: MutableList<DayOfWeek?> = mutableListOf()
) {
    companion object {
        fun from(trainerScheduleInfo: TrainerScheduleInfo?) : RetrieveTrainerDefaultLessonTimeResult {
            return RetrieveTrainerDefaultLessonTimeResult(
                lessonStartTime = dateTimeFormat(trainerScheduleInfo?.lessonStartTime),
                lessonEndTime = dateTimeFormat(trainerScheduleInfo?.lessonEndTime),
                lunchStartTime = dateTimeFormat(trainerScheduleInfo?.lunchStartTime),
                lunchEndTime = dateTimeFormat(trainerScheduleInfo?.lunchEndTime),
                lessonTime = trainerScheduleInfo?.lessonTime?.description,
                closedDays = trainerScheduleInfo?.trainerScheduleClosedDays?.map { it.closedDays }?.toMutableList() ?: mutableListOf()
            )
        }
    }
}

package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.common.TimeFormatter.Companion.dateTimeFormat
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo
import java.time.DayOfWeek

data class TrainerDefaultLessonTimeResult(
    val lessonStartTime: String?,
    val lessonEndTime: String?,
    val lunchStartTime: String? = null,
    val lunchEndTime: String? = null,
    val lessonTime: Int,
    val closedDays: List<DayOfWeek> = mutableListOf()
) {
    companion object {
        fun from(trainerScheduleInfo: TrainerScheduleInfo) : TrainerDefaultLessonTimeResult {
            return TrainerDefaultLessonTimeResult(
                lessonStartTime = dateTimeFormat(trainerScheduleInfo.lessonStartTime),
                lessonEndTime = dateTimeFormat(trainerScheduleInfo.lessonEndTime),
                lunchStartTime = dateTimeFormat(trainerScheduleInfo.lunchStartTime),
                lunchEndTime = dateTimeFormat(trainerScheduleInfo.lunchEndTime),
                lessonTime = trainerScheduleInfo.lessonTime.description,
                closedDays = trainerScheduleInfo.trainerScheduleClosedDays?.map { it.closedDays }?.toList() ?: mutableListOf()
            )
        }
    }
}

package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.common.TimeFormatter.Companion.dateTimeFormat
import com.tobe.healthy.schedule.entity.TrainerScheduleInfo
import java.time.DayOfWeek

data class TrainerScheduleResponse(
    val lessonStartTime: String?,
    val lessonEndTime: String?,
    val lunchStartTime: String? = null,
    val lunchEndTime: String? = null,
    val lessonTime: Int,
    val closedDays: List<DayOfWeek> = mutableListOf()
) {
    companion object {
        fun from(trainerScheduleInfo: TrainerScheduleInfo) : TrainerScheduleResponse {
            return TrainerScheduleResponse(
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

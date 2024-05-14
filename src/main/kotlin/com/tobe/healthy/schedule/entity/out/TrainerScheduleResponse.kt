package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.schedule.domain.entity.LessonTime
import com.tobe.healthy.schedule.entity.TrainerScheduleClosedDaysInfo
import com.tobe.healthy.schedule.entity.TrainerScheduleInfo
import java.time.DayOfWeek
import java.time.LocalTime

data class TrainerScheduleResponse(
    val lessonStartTime: LocalTime,
    val lessonEndTime: LocalTime,
    val lunchStartTime: LocalTime? = null,
    val lunchEndTime: LocalTime? = null,
    val lessonTime: LessonTime,
    val trainerScheduleClosedDays: List<TrainerScheduleClosedDaysResponse> = mutableListOf()
) {
    companion object {
        fun from(trainerScheduleInfo: TrainerScheduleInfo) : TrainerScheduleResponse {
            return TrainerScheduleResponse(
                lessonStartTime = trainerScheduleInfo.lessonStartTime,
                lessonEndTime = trainerScheduleInfo.lessonEndTime,
                lunchStartTime = trainerScheduleInfo.lunchStartTime,
                lunchEndTime = trainerScheduleInfo.lunchEndTime,
                lessonTime = trainerScheduleInfo.lessonTime,
                trainerScheduleClosedDays = trainerScheduleInfo.trainerScheduleClosedDays?.map {
                    TrainerScheduleClosedDaysResponse.from(it)
                }?.toList() ?: mutableListOf()
            )
        }
    }

    data class TrainerScheduleClosedDaysResponse(
        val closedDay: DayOfWeek
    ) {
        companion object {
            fun from(trainerScheduleClosedDaysInfo: TrainerScheduleClosedDaysInfo) : TrainerScheduleClosedDaysResponse {
                return TrainerScheduleClosedDaysResponse(
                    closedDay = trainerScheduleClosedDaysInfo.closedDays
                )
            }
        }
    }
}

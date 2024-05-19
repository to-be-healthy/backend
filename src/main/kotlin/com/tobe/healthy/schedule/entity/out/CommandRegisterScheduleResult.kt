package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.schedule.domain.entity.LessonTime
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.entity.TrainerScheduleInfo
import java.time.LocalDate
import java.time.LocalTime

data class CommandRegisterScheduleResult(
    val lessonDt: MutableList<LocalDate> = mutableListOf(),
    val lessonStartTime: LocalTime,
    val lessonEndTime: LocalTime,
    val lessonTime: LessonTime,
    val lunchStartTime: LocalTime?,
    val lunchEndTime: LocalTime?,
) {
    companion object {
        fun from(
            schedule: List<Schedule>,
            trainerScheduleInfo: TrainerScheduleInfo
        ): CommandRegisterScheduleResult {
            return CommandRegisterScheduleResult(
                lessonDt = schedule.map { it.lessonDt }.distinct().toMutableList(),
                lessonStartTime = trainerScheduleInfo.lessonStartTime,
                lessonEndTime = trainerScheduleInfo.lessonEndTime,
                lessonTime = trainerScheduleInfo.lessonTime,
                lunchStartTime = trainerScheduleInfo.lunchStartTime,
                lunchEndTime = trainerScheduleInfo.lunchEndTime,
            )
        }
    }
}

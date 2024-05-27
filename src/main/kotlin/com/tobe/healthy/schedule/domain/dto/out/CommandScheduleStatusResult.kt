package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

data class CommandScheduleStatusResult(
    val scheduleId: Long,
    val lessonDt: String,
    val lessonTime: String,
    val reservationStatus: ReservationStatus
) {
    companion object {
        fun from(schedule: Schedule) : CommandScheduleStatusResult {
            return CommandScheduleStatusResult(
                scheduleId = schedule.id!!,
                lessonDt = formatLessonDt(schedule.lessonDt),
                lessonTime = formatLessonTime(schedule.lessonStartTime, schedule.lessonEndTime),
                reservationStatus = schedule.reservationStatus
            )
        }
        private fun formatLessonTime(lessonStartTime: LocalTime, lessonEndTime: LocalTime): String {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val startTime = lessonStartTime.format(formatter)
            val endTime = lessonEndTime.format(formatter)
            return "${startTime} - ${endTime}"
        }

        private fun formatLessonDt(lessonDt: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN)
            return lessonDt.format(formatter)
        }
    }
}

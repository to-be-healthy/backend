package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.common.LessonTimeFormatter.formatLessonDt
import com.tobe.healthy.common.LessonTimeFormatter.formatLessonTime
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.Schedule

data class CommandScheduleStatusResult(
    val scheduleId: Long,
    val studentId: Long?,
    val studentName: String?,
    val trainerId: Long?,
    val lessonDt: String?,
    val lessonTime: String,
    val reservationStatus: ReservationStatus
) {
    companion object {
        fun from(schedule: Schedule) : CommandScheduleStatusResult {
            return CommandScheduleStatusResult(
                scheduleId = schedule.id!!,
                studentId = schedule.applicant?.id,
                studentName = schedule.applicant?.name,
                trainerId = schedule.trainer?.id,
                lessonDt = formatLessonDt(schedule.lessonDt),
                lessonTime = formatLessonTime(schedule.lessonStartTime, schedule.lessonEndTime),
                reservationStatus = schedule.reservationStatus
            )
        }
    }
}

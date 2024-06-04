package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.common.LessonTimeFormatter.formatLessonDt
import com.tobe.healthy.common.LessonTimeFormatter.formatLessonTime
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.NO_SHOW
import com.tobe.healthy.schedule.domain.entity.Schedule

data class RetrieveApplicantSchedule(
    val studentId: Long?,
    val studentName: String?,
    val scheduleId: Long,
    val lessonDt: String?,
    val lessonTime: String,
    val attendanceStatus: String
) {
    companion object {
        fun from(schedule: Schedule): RetrieveApplicantSchedule {
            return RetrieveApplicantSchedule(
                studentId = schedule.applicant?.id,
                studentName = schedule.applicant?.name,
                scheduleId = schedule.id,
                lessonDt = formatLessonDt(schedule.lessonDt),
                lessonTime = formatLessonTime(schedule.lessonStartTime, schedule.lessonEndTime),
                attendanceStatus = formatReservationStatus(schedule.reservationStatus)
            )
        }

        private fun formatReservationStatus(reservationStatus: ReservationStatus): String {
            return when (reservationStatus) {
                COMPLETED -> "출석"
                NO_SHOW -> "미출석"
                else -> reservationStatus.description
            }
        }
    }
}
package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.NO_SHOW
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

data class RetrieveUnwrittenLessonHistory(
    val scheduleId: Long,
    val studentId: Long,
    val studentName: String?,
    val lessonDt: String,
    val lessonTime: String,
    val reservationStatus: String,
    val reviewStatus: String
) {
    companion object {
        fun from(schedule: Schedule) : RetrieveUnwrittenLessonHistory {
            return RetrieveUnwrittenLessonHistory(
                scheduleId = schedule.id,
                studentId = schedule.applicant!!.id,
                studentName = schedule.applicant?.name,
                lessonDt = formatLessonDt(schedule.lessonDt),
                lessonTime = formatLessonTime(schedule.lessonStartTime, schedule.lessonEndTime),
                reservationStatus = formatReservationStatus(schedule.reservationStatus),
                reviewStatus = validateReviewStatus(schedule.lessonHistories)
            )
        }

        private fun validateReviewStatus(lessonHistories: List<LessonHistory>): String {
            return if (lessonHistories.isEmpty()) "미작성" else "작성"
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

        private fun formatReservationStatus(reservationStatus: ReservationStatus): String {
            return when (reservationStatus) {
                COMPLETED -> "출석"
                NO_SHOW -> "미출석"
                else -> reservationStatus.description
            }
        }
    }
}

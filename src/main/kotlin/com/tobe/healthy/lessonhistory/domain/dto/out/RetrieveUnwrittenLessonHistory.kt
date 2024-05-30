package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.common.LessonTimeFormatter.formatLessonDt
import com.tobe.healthy.common.LessonTimeFormatter.formatLessonTimeWithAMPM
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.NO_SHOW
import com.tobe.healthy.schedule.domain.entity.Schedule

data class RetrieveUnwrittenLessonHistory(
    val scheduleId: Long,
    val studentId: Long,
    val studentName: String?,
    val lessonDt: String?,
    val lessonTime: String,
    val reservationStatus: String,
    val lessonHistoryId: Long?,
    val reviewStatus: String
) {
    companion object {
        fun from(schedule: Schedule) : RetrieveUnwrittenLessonHistory {
            return RetrieveUnwrittenLessonHistory(
                scheduleId = schedule.id,
                studentId = schedule.applicant!!.id,
                studentName = schedule.applicant?.name,
                lessonDt = formatLessonDt(schedule.lessonDt),
                lessonTime = formatLessonTimeWithAMPM(schedule.lessonStartTime, schedule.lessonEndTime),
                reservationStatus = formatReservationStatus(schedule.reservationStatus),
                lessonHistoryId = schedule.lessonHistories.firstOrNull()?.id,
                reviewStatus = validateReviewStatus(schedule.lessonHistories)
            )
        }

        private fun validateReviewStatus(lessonHistories: List<LessonHistory>): String {
            return if (lessonHistories.isEmpty()) "미작성" else "작성"
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

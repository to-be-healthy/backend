package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.schedule.domain.entity.Schedule

data class RetrieveSimpleLessonHistoryResult(
    val scheduleId: Long,
    val lessonDt: String,
    val lessonTime: String,
    val attendanceStatus: String,
    val studentId: Long?
) {
    companion object {
        fun from(entity: Schedule): RetrieveSimpleLessonHistoryResult {
            return RetrieveSimpleLessonHistoryResult(
                scheduleId = entity.id,
                lessonDt = entity.lessonDt.toString(),
                lessonTime = "${entity.lessonStartTime} - ${entity.lessonEndTime}",
                attendanceStatus = entity.reservationStatus.description,
                studentId = entity.applicant?.id
            )
        }
    }
}

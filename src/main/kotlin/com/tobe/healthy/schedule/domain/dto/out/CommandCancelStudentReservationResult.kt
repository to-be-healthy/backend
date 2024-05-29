package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalTime

data class CommandCancelStudentReservationResult(
    val studentId: Long? = null,
    val trainerId: Long,
    val scheduleId: Long,
    val lessonStartTime: LocalTime,
    val waitingStudentId: Long? = null
) {
    companion object {
        fun from(schedule: Schedule) : CommandCancelStudentReservationResult {
            return CommandCancelStudentReservationResult(
                studentId = schedule.applicant?.id,
                trainerId = schedule.trainer.id,
                scheduleId = schedule.id,
                lessonStartTime = schedule.lessonStartTime,
                waitingStudentId = schedule.scheduleWaiting?.firstOrNull()?.member?.id
            )
        }
    }
}
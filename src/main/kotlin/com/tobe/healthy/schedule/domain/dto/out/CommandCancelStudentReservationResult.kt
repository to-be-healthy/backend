package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalTime

data class CommandCancelStudentReservationResult(
    val studentId: Long? = null,
    val studentName: String?,
    val trainerId: Long,
    val trainerName: String,
    val scheduleId: Long,
    val lessonStartTime: LocalTime,
    val waitingStudentId: Long? = null
) {
    companion object {
        fun from(schedule: Schedule, applicant: Member?) : CommandCancelStudentReservationResult {
            return CommandCancelStudentReservationResult(
                studentId = applicant?.id,
                studentName = applicant?.name,
                trainerId = schedule.trainer.id,
                trainerName = "${schedule.trainer.name} 트레이너",
                scheduleId = schedule.id,
                lessonStartTime = schedule.lessonStartTime,
                waitingStudentId = schedule.scheduleWaiting?.firstOrNull()?.member?.id
            )
        }
    }
}
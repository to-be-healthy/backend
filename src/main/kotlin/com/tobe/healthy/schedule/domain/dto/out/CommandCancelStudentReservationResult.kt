package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalTime

data class CommandCancelStudentReservationResult(
    val scheduleId: Long,
    val lessonStartTime: LocalTime,
    val lessonEndTime: LocalTime,
    val trainerId: Long,
    val trainerName: String,
    val studentId: Long?,
    val studentName: String?,
    val waitingStudentId: Long? = null
) {
    companion object {
        fun from(schedule: Schedule, applicant: Member?) : CommandCancelStudentReservationResult {
            return CommandCancelStudentReservationResult(
                scheduleId = schedule.id,
                lessonStartTime = schedule.lessonStartTime,
                lessonEndTime = schedule.lessonEndTime,
                trainerId = schedule.trainer.id,
                trainerName = "${schedule.trainer.name} 트레이너",
                studentId = applicant?.id,
                studentName = applicant?.name,
                waitingStudentId = schedule.scheduleWaiting?.firstOrNull()?.member?.id
            )
        }
    }
}
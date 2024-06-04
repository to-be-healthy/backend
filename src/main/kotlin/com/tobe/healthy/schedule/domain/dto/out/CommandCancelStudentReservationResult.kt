package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalTime

data class CommandCancelStudentReservationResult(
    val scheduleId: Long,
    val lessonStartTime: LocalTime,
    val lessonEndTime: LocalTime,
    val trainerId: Long,
    val trainerName: String,
    val studentId: Long?,
    val studentName: String?
) {
    companion object {
        fun from(schedule: Schedule) : CommandCancelStudentReservationResult {
            return CommandCancelStudentReservationResult(
                scheduleId = schedule.id,
                lessonStartTime = schedule.lessonStartTime,
                lessonEndTime = schedule.lessonEndTime,
                trainerId = schedule.trainer.id,
                trainerName = "${schedule.trainer.name} 트레이너",
                studentId = schedule.scheduleWaiting?.firstOrNull()?.member?.id,
                studentName = schedule.scheduleWaiting?.firstOrNull()?.member?.name
            )
        }
    }
}
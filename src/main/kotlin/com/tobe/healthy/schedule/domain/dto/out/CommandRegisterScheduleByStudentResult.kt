package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalTime

data class CommandRegisterScheduleByStudentResult(
    val scheduleId: Long,
    val lessonDt: LocalDate,
    val lessonStartTime: LocalTime,
    val lessonEndTime: LocalTime,
    val studentName: String,
    val studentId: Long,
    val trainerId: Long
) {
    companion object {
        fun from(
            schedule: Schedule,
            student: Member
        ): CommandRegisterScheduleByStudentResult {
            return CommandRegisterScheduleByStudentResult(
                scheduleId = schedule.id,
                lessonDt = schedule.lessonDt,
                lessonStartTime = schedule.lessonStartTime,
                lessonEndTime = schedule.lessonEndTime,
                studentId = student.id,
                studentName = student.name,
                trainerId = schedule.trainer.id
            )
        }
    }
}
package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalTime

data class RegisterScheduleForStudentResponse(
    val scheduleId: Long,
    val lessonDt: LocalDate,
    val lessonStartTime: LocalTime,
    val lessonEndTime: LocalTime,
    val studentName: String
) {
    companion object {
        fun from(schedule: Schedule, student: Member) : RegisterScheduleForStudentResponse {
            return RegisterScheduleForStudentResponse(
                scheduleId = schedule.id,
                lessonDt = schedule.lessonDt,
                lessonStartTime = schedule.lessonStartTime,
                lessonEndTime = schedule.lessonEndTime,
                studentName = student.name
            )
        }
    }
}
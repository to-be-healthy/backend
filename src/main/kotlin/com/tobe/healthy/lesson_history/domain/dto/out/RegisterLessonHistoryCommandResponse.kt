package com.tobe.healthy.lesson_history.domain.dto.out

import com.tobe.healthy.lesson_history.domain.entity.LessonHistory
import java.time.LocalDate
import java.time.LocalTime

data class RegisterLessonHistoryCommandResponse(
    val lessonHistoryId: Long,
    val title: String,
    val content: String,
    val scheduleId: Long,
    val lessonDt: LocalDate,
    val lessonStartTime: LocalTime,
    val lessonEndTime: LocalTime,
    val trainerId: Long,
    val trainerName: String,
    val studentId: Long,
    val studentName: String,
) {
    companion object {
        fun from(lessonHistory: LessonHistory): RegisterLessonHistoryCommandResponse {
            return RegisterLessonHistoryCommandResponse(
                lessonHistoryId = lessonHistory.id,
                title = lessonHistory.title,
                content = lessonHistory.content,
                scheduleId = lessonHistory.schedule.id,
                lessonDt = lessonHistory.schedule.lessonDt,
                lessonStartTime = lessonHistory.schedule.lessonStartTime,
                lessonEndTime = lessonHistory.schedule.lessonEndTime,
                trainerId = lessonHistory.trainer.id,
                trainerName = lessonHistory.trainer.name,
                studentId = lessonHistory.student.id,
                studentName = lessonHistory.student.name
            )
        }
    }
}

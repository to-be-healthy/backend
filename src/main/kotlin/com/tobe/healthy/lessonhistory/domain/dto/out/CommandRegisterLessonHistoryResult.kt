package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import java.time.LocalDate
import java.time.LocalTime

data class CommandRegisterLessonHistoryResult(
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
        fun from(lessonHistory: LessonHistory): CommandRegisterLessonHistoryResult {
            return CommandRegisterLessonHistoryResult(
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

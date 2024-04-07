package com.tobe.healthy.lessonHistory.domain.dto

import com.tobe.healthy.file.domain.entity.Profile
import com.tobe.healthy.lessonHistory.domain.entity.AttendanceStatus.ABSENT
import com.tobe.healthy.lessonHistory.domain.entity.AttendanceStatus.ATTENDED
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors.toList

data class LessonHistoryCommandResult(
    val id: Long,
    val title: String,
    val content: String,
    val comment: MutableList<LessonHistoryCommentCommandResult>,
    val createdAt: LocalDateTime,
    val student: String,
    val trainer: String,
    val lessonDt: String,
    val lessonTime: String,
    val attendanceStatus: String,
    val fileUrl: MutableList<String>
) {

    companion object {
        fun from(entity: LessonHistory, files: MutableList<Profile>): LessonHistoryCommandResult {
            return LessonHistoryCommandResult(
                id = entity.id ?: throw IllegalArgumentException("LessonHistory ID cannot be null"),
                title = entity.title,
                content = entity.content,
                comment = entity.lessonHistoryComment?.map { LessonHistoryCommentCommandResult.from(it) }
                    ?.toMutableList()
                    ?: mutableListOf(),
                createdAt = entity.createdAt,
                student = entity.student.name,
                trainer = "${entity.trainer.name} 트레이너",
                lessonDt = formatLessonDt(entity.schedule.lessonDt),
                lessonTime = formatLessonTime(entity.schedule.lessonStartTime, entity.schedule.lessonEndTime),
                attendanceStatus = validateAttendanceStatus(entity.schedule.lessonDt, entity.schedule.lessonEndTime),
                fileUrl = files.stream().filter { file -> file.lessonHistory.id == entity.id}.map { file -> file.fileUrl }.collect(toList())
            )
        }

        private fun validateAttendanceStatus(lessonDt: LocalDate, lessonEndTime: LocalTime): String {
            val lesson = LocalDateTime.of(lessonDt, lessonEndTime)
            if (LocalDateTime.now().isAfter(lesson)) {
                return ATTENDED.description
            }
            return ABSENT.description
        }

        private fun formatLessonTime(lessonStartTime: LocalTime, lessonEndTime: LocalTime): String {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val startTime = lessonStartTime.format(formatter)
            val endTime = lessonEndTime.format(formatter)
            return "${startTime} - ${endTime}"
        }

        private fun formatLessonDt(lessonDt: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN)
            return lessonDt.format(formatter)
        }
    }

    data class LessonHistoryCommentCommandResult(
        val id: Long,
        val content: String,
        val writer: Long,
        val order: Int,
        val parentId: Long?
    ) {
        companion object {
            fun from(entity: LessonHistoryComment): LessonHistoryCommentCommandResult {
                return LessonHistoryCommentCommandResult(
                    id = entity.id ?: throw IllegalArgumentException("Comment ID cannot be null"),
                    content = entity.content,
                    writer = entity.writer.id,
                    order = entity.order,
                    parentId = entity.parentId?.id
                )
            }
        }
    }
}
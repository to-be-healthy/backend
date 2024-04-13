package com.tobe.healthy.lessonHistory.domain.dto

import com.tobe.healthy.file.domain.entity.AwsS3File
import com.tobe.healthy.lessonHistory.domain.entity.AttendanceStatus.ABSENT
import com.tobe.healthy.lessonHistory.domain.entity.AttendanceStatus.ATTENDED
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.stream.Collectors.toList

data class LessonHistoryCommandResult(
    val id: Long,
    val title: String,
    val content: String,
    val comments: MutableList<LessonHistoryCommentCommandResult> = mutableListOf(),
    val createdAt: LocalDateTime,
    val student: String,
    val trainer: String,
    val lessonDt: String,
    val lessonTime: String,
    val attendanceStatus: String,
    val files: MutableList<LessonHistoryFileResults> = mutableListOf()
) {

    companion object {
        fun from(entity: LessonHistory): LessonHistoryCommandResult {
            return LessonHistoryCommandResult(
                id = entity.id,
                title = entity.title,
                content = entity.content,
                comments = sortLessonHistoryComment(entity.lessonHistoryComment),
                createdAt = entity.createdAt,
                student = entity.student.name,
                trainer = "${entity.trainer.name} 트레이너",
                lessonDt = formatLessonDt(entity.schedule.lessonDt),
                lessonTime = formatLessonTime(entity.schedule.lessonStartTime, entity.schedule.lessonEndTime),
                attendanceStatus = validateAttendanceStatus(entity.schedule.lessonDt, entity.schedule.lessonEndTime),
                files = entity.file.map(LessonHistoryFileResults::from).sortedBy { it.fileOrder }.toMutableList()
            )
        }

        fun sortLessonHistoryComment(comment: List<LessonHistoryComment>): MutableList<LessonHistoryCommentCommandResult> {
            val (topLevelComments, replies) = comment.sortedBy{ it.order }.partition { it.parentId == null }

            topLevelComments.forEach { parent ->
                parent.replies = replies.filter { child ->
                    child.parentId?.id == parent.id
                }.sortedBy { it.order }.toMutableList()
            }

            return topLevelComments.map { LessonHistoryCommentCommandResult.from(it) }.toMutableList()

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
        val replies: MutableList<LessonHistoryComment> = mutableListOf(),
        val files: MutableList<LessonHistoryFileResults> = mutableListOf()
    ) {
        companion object {
            fun from(entity: LessonHistoryComment): LessonHistoryCommentCommandResult {
                return LessonHistoryCommentCommandResult(
                    id = entity.id,
                    content = entity.content,
                    writer = entity.writer.id,
                    order = entity.order,
                    replies = entity.replies,
                    files = entity.files.stream().map(LessonHistoryFileResults::from).collect(toList())
                )
            }
        }
    }

    data class LessonHistoryFileResults(
        val originalFileName: String,
        val fileUrl: String,
        val fileOrder: Int,
        val createdAt: LocalDateTime
    ) {
        companion object {
            fun from(entity: AwsS3File): LessonHistoryFileResults {
                return LessonHistoryFileResults(
                    originalFileName = entity.originalFileName,
                    fileUrl = entity.fileUrl,
                    fileOrder = entity.fileOrder,
                    createdAt = entity.createdAt
                )
            }
        }
    }
}

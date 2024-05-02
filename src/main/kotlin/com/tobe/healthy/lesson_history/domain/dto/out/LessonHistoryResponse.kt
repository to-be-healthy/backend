package com.tobe.healthy.lesson_history.domain.dto.out

import com.tobe.healthy.file.AwsS3File
import com.tobe.healthy.lesson_history.domain.entity.AttendanceStatus.ABSENT
import com.tobe.healthy.lesson_history.domain.entity.AttendanceStatus.ATTENDED
import com.tobe.healthy.lesson_history.domain.entity.LessonHistory
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Schema(description = "수업 일지")
data class LessonHistoryResponse(
    @Schema(description = "수업 일지 ID", example = "1")
    val id: Long,
    @Schema(description = "수업 일지 제목", example = "홍길동님 수업 일지입니다!")
    val title: String,
    @Schema(description = "수업 일지 내용", example = "오늘도 고생하셨습니다^^ 처음보다~")
    val content: String,
    @Schema(description = "수업 일지 총 댓글 수", example = "30")
    val commentTotalCount: Int,
    @Schema(description = "수업 일지 등록일")
    val createdAt: LocalDateTime,
    @Schema(description = "수업 일지 대상 학생", example = "아무개")
    val student: String,
    @Schema(description = "수업 일지 작성한 트레이너", example = "홍길동")
    val trainer: String,
    @Schema(description = "일정 ID", example = "1")
    val scheduleId: Long,
    @Schema(description = "수업 일자", example = "yy:mm:dd")
    val lessonDt: String,
    @Schema(description = "수업 시간", example = "10:00 ~ 10:50")
    val lessonTime: String,
    @Schema(description = "수업 참석 여부", example = "참석/미참석")
    val attendanceStatus: String,
    @Schema(description = "수업 일지 첨부파일")
    val files: MutableList<LessonHistoryFileResults> = mutableListOf(),
) {

    companion object {
        fun from(entity: LessonHistory): LessonHistoryResponse {
            return LessonHistoryResponse(
                id = entity.id,
                title = entity.title,
                content = entity.content,
                commentTotalCount = entity.lessonHistoryComment.count(),
                createdAt = entity.createdAt,
                student = entity.student.name,
                trainer = "${entity.trainer.name} 트레이너",
                scheduleId = entity.schedule.id,
                lessonDt = formatLessonDt(entity.schedule.lessonDt),
                lessonTime = formatLessonTime(entity.schedule.lessonStartTime, entity.schedule.lessonEndTime),
                attendanceStatus = validateAttendanceStatus(entity.schedule.lessonDt, entity.schedule.lessonEndTime),
                files = entity.file.map(LessonHistoryFileResults.Companion::from).sortedBy { it.fileOrder }
                    .toMutableList(),
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

    @Schema(description = "수업 일지 첨부파일")
    data class LessonHistoryFileResults(
        @Schema(description = "첨부한 파일 이름", example = "정선우 학생 운동 사진")
        val originalFileName: String,
        @Schema(description = "첨부한 파일 URL(AWS S3)", example = "https://~~~")
        val fileUrl: String,
        @Schema(description = "첨부한 파일 순서", example = "0")
        val fileOrder: Int,
        @Schema(description = "파일 등록 날짜", example = "2024-04-10 13:00:12")
        val createdAt: LocalDateTime,
    ) {
        companion object {
            fun from(entity: AwsS3File): LessonHistoryFileResults {
                return LessonHistoryFileResults(
                    originalFileName = entity.originalFileName,
                    fileUrl = entity.fileUrl,
                    fileOrder = entity.fileOrder,
                    createdAt = entity.createdAt,
                )
            }
        }
    }
}

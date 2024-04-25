package com.tobe.healthy.lessonHistory.domain.dto.out

import com.tobe.healthy.file.domain.entity.AwsS3File
import com.tobe.healthy.lessonHistory.domain.entity.AttendanceStatus.ABSENT
import com.tobe.healthy.lessonHistory.domain.entity.AttendanceStatus.ATTENDED
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.member.domain.entity.Member
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Schema(description = "수업 일지 상세 조회 응답 DTO")
data class LessonHistoryDetailResponse(
    @Schema(description = "수업일지 ID", example = "1")
    val id: Long,
    @Schema(description = "조회한 수업일지 제목", example = "홍홍홍홍홍ㅋㅋ")
    val title: String,
    @Schema(description = "조회한 수업일지 내용", example = "홍박사 댕꿀잼ㅋㅋ")
    val content: String,
    @Schema(description = "조회한 수업일지 댓글", example = "유튜브 이즈 갓")
    val comments: MutableList<LessonHistoryCommentCommandResult> = mutableListOf(),
    @Schema(description = "조회한 수업일지 댓글", example = "유튜브 이즈 갓")
    val commentTotalCount: Int,
    val createdAt: LocalDateTime,
    val student: String,
    val trainer: String,
    val lessonDt: String,
    val lessonTime: String,
    val attendanceStatus: String,
    val files: MutableList<LessonHistoryFileResults> = mutableListOf()
) {

    companion object {
        fun from(entity: LessonHistory): LessonHistoryDetailResponse {
            return LessonHistoryDetailResponse(
                id = entity.id,
                title = entity.title,
                content = entity.content,
                commentTotalCount = entity.lessonHistoryComment.count(),
                createdAt = entity.createdAt,
                student = entity.student.name,
                trainer = "${entity.trainer.name} 트레이너",
                lessonDt = formatLessonDt(entity.schedule.lessonDt),
                lessonTime = formatLessonTime(entity.schedule.lessonStartTime, entity.schedule.lessonEndTime),
                attendanceStatus = validateAttendanceStatus(entity.schedule.lessonDt, entity.schedule.lessonEndTime),
                files = entity.file.map(LessonHistoryFileResults.Companion::from).sortedBy { it.fileOrder }.toMutableList()
            )
        }

        fun detailFrom(entity: LessonHistory): LessonHistoryDetailResponse {
            val comments = sortLessonHistoryComment(entity.lessonHistoryComment)
            return LessonHistoryDetailResponse(
                id = entity.id,
                title = entity.title,
                content = entity.content,
                comments = comments,
                commentTotalCount = entity.lessonHistoryComment.count(),
                createdAt = entity.createdAt,
                student = entity.student.name,
                trainer = "${entity.trainer.name} 트레이너",
                lessonDt = formatLessonDt(entity.schedule.lessonDt),
                lessonTime = formatLessonTime(entity.schedule.lessonStartTime, entity.schedule.lessonEndTime),
                attendanceStatus = validateAttendanceStatus(entity.schedule.lessonDt, entity.schedule.lessonEndTime),
                files = entity.file.filter { it.lessonHistoryComment == null }
                                   .map(LessonHistoryFileResults.Companion::from).sortedBy { it.fileOrder }
                                   .toMutableList()
            )
        }

        private fun sortLessonHistoryComment(comment: List<LessonHistoryComment>): MutableList<LessonHistoryCommentCommandResult> {
            val (comments, replies) = comment.sortedBy{ it.order }.partition { it.parentId == null }

            comments.forEach { parent ->
                parent.replies = replies.filter { child -> child.parentId?.id == parent.id }
                                        .sortedBy { it.order }
                                        .toMutableList()
            }

            return comment.map { LessonHistoryCommentCommandResult.from(it) }.toMutableList()
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
        val member: LessonHistoryCommentMemberResult,
        val orderNum: Int,
        val replies: MutableList<LessonHistoryCommentCommandResult>?,
        val files: MutableList<LessonHistoryFileResults> = mutableListOf(),
        val delYn: Boolean,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    ) {
        companion object {
            fun from(entity: LessonHistoryComment): LessonHistoryCommentCommandResult {
                return LessonHistoryCommentCommandResult(
                    id = entity.id,
                    content = entity.content,
                    member = LessonHistoryCommentMemberResult.from(entity.writer),
                    orderNum = entity.order,
                    replies = entity.replies?.let { it -> it.map { from(it) } }?.toMutableList(),
                    files = entity.files.map { LessonHistoryFileResults.from(it) }.toMutableList(),
                    delYn = entity.delYn,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    data class LessonHistoryCommentMemberResult(
        val memberId: Long,
        val name: String,
        val fileUrl: String? = null
    ) {
        companion object {
            fun from(entity: Member): LessonHistoryCommentMemberResult {
                return LessonHistoryCommentMemberResult(
                    memberId = entity.id,
                    name = entity.name,
                    fileUrl = entity.profileId?.fileUrl?.let { it }
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

package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonAttendanceStatus.ABSENT
import com.tobe.healthy.lessonhistory.domain.entity.LessonAttendanceStatus.ATTENDED
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles
import com.tobe.healthy.member.domain.entity.Member
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Schema(description = "수업 일지 상세 조회 응답 DTO")
data class RetrieveLessonHistoryDetailResult(
    val id: Long?,
    val title: String?,
    val content: String?,
    val comments: MutableList<LessonHistoryCommentCommandResult?> = mutableListOf(),
    val commentTotalCount: Int?,
    val createdAt: LocalDateTime?,
    val student: String?,
    val trainer: String?,
    val scheduleId: Long?,
    val lessonDt: String?,
    val lessonTime: String?,
    val attendanceStatus: String,
    val files: MutableList<LessonHistoryFileResults> = mutableListOf()
) {

    companion object {
        fun detailFrom(entity: LessonHistory?): RetrieveLessonHistoryDetailResult? {
            return RetrieveLessonHistoryDetailResult(
                id = entity?.id,
                title = entity?.title,
                content = entity?.content,
                comments = sortLessonHistoryComment(entity?.lessonHistoryComment),
                commentTotalCount = entity?.lessonHistoryComment?.count { !it.delYn } ?: 0,
                createdAt = entity?.createdAt,
                student = entity?.student?.name,
                trainer = entity?.trainer?.name?.let { name -> "$name 트레이너" },
                scheduleId = entity?.schedule?.id,
                lessonDt = formatLessonDt(entity?.schedule?.lessonDt),
                lessonTime = formatLessonTime(entity?.schedule?.lessonStartTime, entity?.schedule?.lessonEndTime),
                attendanceStatus = validateAttendanceStatus(
                    entity?.schedule?.lessonDt,
                    entity?.schedule?.lessonEndTime
                ),
                files = entity?.let {
                    it.files.filter {
                        comment -> comment.lessonHistoryComment == null
                    }.map {
                        files -> LessonHistoryFileResults.from(files)
                    }.sortedBy {
                        file -> file.fileOrder
                    }.toMutableList()
                } ?: mutableListOf()
            )
        }

        private fun sortLessonHistoryComment(comments: MutableList<LessonHistoryComment>?): MutableList<LessonHistoryCommentCommandResult?> {
            val nonNullComments = comments ?: mutableListOf()

            val (parent, child) = nonNullComments.sortedBy { it.order }.partition { it.parent == null }

            parent.forEach { parentComment ->
                parentComment.replies = child.filter { child -> child.parent?.id == parentComment.id }
                    .sortedBy { it.order }
                    .toMutableList()
            }
            return parent.map { parentComment -> LessonHistoryCommentCommandResult.from(parentComment) }.toMutableList()
        }

        private fun validateAttendanceStatus(lessonDt: LocalDate?, lessonEndTime: LocalTime?): String {
            val lesson = LocalDateTime.of(lessonDt, lessonEndTime)
            if (LocalDateTime.now().isAfter(lesson)) {
                return ATTENDED.description
            }
            return ABSENT.description
        }

        private fun formatLessonTime(lessonStartTime: LocalTime?, lessonEndTime: LocalTime?): String {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val startTime = lessonStartTime?.format(formatter)
            val endTime = lessonEndTime?.format(formatter)
            return "${startTime} - ${endTime}"
        }

        private fun formatLessonDt(lessonDt: LocalDate?): String? {
            val formatter = DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN)
            return lessonDt?.format(formatter)
        }
    }

    data class LessonHistoryCommentCommandResult(
        val id: Long?,
        val content: String?,
        val member: LessonHistoryCommentMemberResult?,
        val orderNum: Int?,
        val parentId: Long?,
        val replies: MutableList<LessonHistoryCommentCommandResult> = mutableListOf(),
        val files: MutableList<LessonHistoryFileResults> = mutableListOf(),
        val delYn: Boolean?,
        val createdAt: LocalDateTime?,
        val updatedAt: LocalDateTime?
    ) {
        companion object {
            fun from(entity: LessonHistoryComment?): LessonHistoryCommentCommandResult {
                return LessonHistoryCommentCommandResult(
                    id = entity?.id,
                    content = if (entity?.delYn == true) "삭제된 댓글입니다." else entity?.content,
                    member = LessonHistoryCommentMemberResult.from(entity?.writer),
                    orderNum = entity?.order,
                    replies = entity?.replies?.map { replies -> from(replies) }?.toMutableList() ?: mutableListOf(),
                    parentId = entity?.parent?.id,
                    files = entity?.let {
                        it.files.map {
                            files -> LessonHistoryFileResults.from(files)
                        }?.toMutableList()
                    } ?: mutableListOf(),
                    delYn = entity?.delYn,
                    createdAt = entity?.createdAt,
                    updatedAt = entity?.updatedAt
                )
            }
        }
    }

    data class LessonHistoryCommentMemberResult(
        val memberId: Long?,
        val name: String?,
        val fileUrl: String? = null
    ) {
        companion object {
            fun from(entity: Member?): LessonHistoryCommentMemberResult {
                return LessonHistoryCommentMemberResult(
                    memberId = entity?.id,
                    name = entity?.name,
                    fileUrl = entity?.memberProfile?.fileUrl
                )
            }
        }
    }

    data class LessonHistoryFileResults(
        val fileUrl: String?,
        val fileOrder: Int?,
        val createdAt: LocalDateTime?
    ) {
        companion object {
            fun from(entity: LessonHistoryFiles?): LessonHistoryFileResults {
                return LessonHistoryFileResults(
                    fileUrl = entity?.fileUrl,
                    fileOrder = entity?.fileOrder,
                    createdAt = entity?.createdAt
                )
            }
        }
    }
}

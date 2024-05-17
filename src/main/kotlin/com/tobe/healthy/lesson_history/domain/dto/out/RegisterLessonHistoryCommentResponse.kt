package com.tobe.healthy.lesson_history.domain.dto.out

import com.tobe.healthy.lesson_history.domain.entity.LessonHistoryComment

data class RegisterLessonHistoryCommentResponse(
    val lessonHistoryId: Long,
    val lessonHistoryCommentId: Long,
    val writerId: Long,
    val writerName: String,
    val comment: String,
) {
    companion object {
        fun from(lessonHistoryComment: LessonHistoryComment) : RegisterLessonHistoryCommentResponse {
            return RegisterLessonHistoryCommentResponse(
                lessonHistoryId = lessonHistoryComment.lessonHistory.id,
                lessonHistoryCommentId = lessonHistoryComment.id,
                writerId = lessonHistoryComment.writer.id,
                writerName = lessonHistoryComment.writer.name,
                comment = lessonHistoryComment.content
            )
        }
    }
}

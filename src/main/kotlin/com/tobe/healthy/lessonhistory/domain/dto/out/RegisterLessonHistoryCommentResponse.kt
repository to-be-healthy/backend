package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment

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

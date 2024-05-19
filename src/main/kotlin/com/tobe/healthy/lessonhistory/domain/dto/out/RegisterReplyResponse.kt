package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment

data class RegisterReplyResponse(
    val lessonHistoryId: Long,
    val commentId: Long,
    val content: String,
    val order: Int,
    val delYn: Boolean,
    val parentId: Long?
) {
    companion object {
        fun from(lessonHistoryComment: LessonHistoryComment) : RegisterReplyResponse {
            return RegisterReplyResponse(
                lessonHistoryId = lessonHistoryComment.lessonHistory.id,
                commentId = lessonHistoryComment.id,
                content = lessonHistoryComment.content,
                order = lessonHistoryComment.order,
                delYn = lessonHistoryComment.delYn,
                parentId = lessonHistoryComment.parent?.id
            )
        }
    }
}

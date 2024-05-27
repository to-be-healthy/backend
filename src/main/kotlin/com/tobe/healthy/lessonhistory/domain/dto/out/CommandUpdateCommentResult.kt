package com.tobe.healthy.lessonhistory.domain.dto.out

data class CommandUpdateCommentResult(
    val lessonHistoryId: Long?,
    val lessonHistoryCommentId: Long?,
    val content: String,
) {
    companion object {
        fun from(
            lessonHistoryId: Long?,
            lessonHistoryCommentId: Long?,
            content: String
        ): CommandUpdateCommentResult {
            return CommandUpdateCommentResult(
                lessonHistoryId = lessonHistoryId,
                lessonHistoryCommentId = lessonHistoryCommentId,
                content = content
            )
        }
    }
}

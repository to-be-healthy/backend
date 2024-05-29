package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment

data class CommandUpdateCommentResult(
    val lessonHistoryId: Long?,
    val lessonHistoryCommentId: Long?,
    val content: String,
    val files: MutableList<CommandUploadFileResult> = mutableListOf()
) {
    companion object {
        fun from(lessonHistoryComment: LessonHistoryComment): CommandUpdateCommentResult {
            return CommandUpdateCommentResult(
                lessonHistoryId = lessonHistoryComment.lessonHistory?.id,
                lessonHistoryCommentId = lessonHistoryComment.id,
                content = lessonHistoryComment.content,
                files = lessonHistoryComment.files.map { CommandUploadFileResult.from(it) }.toMutableList()
            )
        }
    }
}

package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles

data class CommandRegisterReplyResult(
    val lessonHistoryId: Long?,
    val commentId: Long?,
    val content: String,
    val files: MutableList<CommandUploadFileResult> = mutableListOf(),
    val order: Int,
    val delYn: Boolean,
    val parentId: Long?
) {
    companion object {
        fun from(lessonHistoryComment: LessonHistoryComment, files: MutableList<LessonHistoryFiles>) : CommandRegisterReplyResult {
            return CommandRegisterReplyResult(
                lessonHistoryId = lessonHistoryComment.lessonHistory?.id,
                commentId = lessonHistoryComment?.id,
                content = lessonHistoryComment.content,
                files = files.map { CommandUploadFileResult.from(it) }.toMutableList(),
                order = lessonHistoryComment.order,
                delYn = lessonHistoryComment.delYn,
                parentId = lessonHistoryComment.parent?.id
            )
        }
    }
}

package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles

data class CommandRegisterCommentResult(
    val lessonHistoryId: Long?,
    val lessonHistoryCommentId: Long?,
    val writerId: Long?,
    val writerName: String?,
    val content: String,
    val files: MutableList<CommandUploadFileResult> = mutableListOf()
) {
    companion object {
        fun from(lessonHistoryComment: LessonHistoryComment, files: MutableList<LessonHistoryFiles>) : CommandRegisterCommentResult {
            return CommandRegisterCommentResult(
                lessonHistoryId = lessonHistoryComment.lessonHistory?.id,
                lessonHistoryCommentId = lessonHistoryComment.id,
                writerId = lessonHistoryComment.writer?.id,
                writerName = lessonHistoryComment.writer?.name,
                content = lessonHistoryComment.content,
                files = files.map { CommandUploadFileResult.from(it) }.toMutableList()
            )
        }
    }
}

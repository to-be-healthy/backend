package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles

data class CommandUpdateLessonHistoryResult(
    val lessonHistoryId: Long?,
    val title: String,
    val content: String,
    val files: MutableList<CommandUploadFileResult> = mutableListOf()
) {
    companion object {
        fun from(lessonHistory: LessonHistory, files: MutableList<LessonHistoryFiles>): CommandUpdateLessonHistoryResult {
            return CommandUpdateLessonHistoryResult(
                lessonHistoryId = lessonHistory.id,
                title = lessonHistory.title,
                content = lessonHistory.content,
                files = files.map { CommandUploadFileResult.from(it) }.toMutableList()
            )
        }
    }
}

package com.tobe.healthy.lessonhistory.domain.dto.out

data class CommandUpdateLessonHistoryResult(
    val lessonHistoryId: Long?,
    val title: String,
    val content: String
) {
    companion object {
        fun from(lessonHistoryId: Long?, title: String, content: String): CommandUpdateLessonHistoryResult {
            return CommandUpdateLessonHistoryResult(
                lessonHistoryId = lessonHistoryId,
                title = title,
                content = content
            )
        }
    }
}

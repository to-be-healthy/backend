package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles

data class CommandUploadFileResult(
    val fileUrl: String,
    val fileOrder: Int
) {
    companion object {
        fun from(lessonHistoryFiles: LessonHistoryFiles): CommandUploadFileResult {
            return CommandUploadFileResult(
                lessonHistoryFiles.fileUrl,
                lessonHistoryFiles.fileOrder
            )
        }
    }
}

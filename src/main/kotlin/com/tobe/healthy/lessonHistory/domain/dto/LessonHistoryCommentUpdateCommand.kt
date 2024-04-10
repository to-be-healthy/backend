package com.tobe.healthy.lessonHistory.domain.dto

import jakarta.validation.constraints.NotBlank

data class LessonHistoryCommentUpdateCommand(
    @field:NotBlank(message = "내용을 입력해 주세요.")
    val content: String?
)

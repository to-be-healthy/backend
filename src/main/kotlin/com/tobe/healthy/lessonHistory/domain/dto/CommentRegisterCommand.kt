package com.tobe.healthy.lessonHistory.domain.dto

import jakarta.validation.constraints.NotBlank

data class CommentRegisterCommand(
    @field:NotBlank(message = "내용을 입력해 주세요.")
    val comment: String?,
)

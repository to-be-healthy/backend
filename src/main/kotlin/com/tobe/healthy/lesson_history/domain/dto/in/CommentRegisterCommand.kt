package com.tobe.healthy.lesson_history.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "댓글 등록 DTO")
data class CommentRegisterCommand(
    @Schema(description = "등록할 댓글 내용")
    @field:NotBlank(message = "내용을 입력해 주세요.")
    val comment: String?,
)

package com.tobe.healthy.lesson_history.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "수업 일지 수정 DTO")
data class LessonHistoryCommentCommand(
    @Schema(description = "수정할 댓글 내용", example = "트레이너님 휴대폰 그만봐", required = true)
    @field:NotBlank(message = "내용을 입력해 주세요.")
    val content: String
)

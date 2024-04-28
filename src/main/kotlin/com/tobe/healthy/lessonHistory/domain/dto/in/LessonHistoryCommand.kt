package com.tobe.healthy.lessonHistory.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class LessonHistoryCommand(
    @Schema(description = "수정할 수업일지 제목")
    @field:NotBlank(message = "수정할 수업일지 제목을 입력해 주세요.")
    val title: String?,

    @Schema(description = "수정할 수업일지 내용")
    @field:NotBlank(message = "수정할 수업일지 내용을 입력해 주세요.")
    val content: String?
)

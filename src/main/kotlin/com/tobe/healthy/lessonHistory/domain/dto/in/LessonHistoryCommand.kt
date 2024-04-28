package com.tobe.healthy.lessonHistory.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class LessonHistoryCommand(
    @Schema(description = "수정할 수업일지 제목")
    @field:NotBlank(message = "수정할 수업일지 제목을 입력해 주세요.")
    val title: String?,

    @field:NotBlank(message = "수정할 수업일지 내용")
    @NotEmpty(message = "수정할 수업일지 내용을 입력해 주세요.")
    val content: String?
)

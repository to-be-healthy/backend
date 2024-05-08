package com.tobe.healthy.lesson_history.domain.dto.`in`

import com.tobe.healthy.lesson_history.domain.dto.out.UploadFileResponse
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class LessonHistoryCommand(
    @Schema(description = "수정할 수업일지 제목")
    @field:NotBlank(message = "수정할 수업일지 제목을 입력해 주세요.")
    val title: String?,

    @Schema(description = "수정할 수업일지 내용")
    @field:NotBlank(message = "수정할 수업일지 내용을 입력해 주세요.")
    val content: String?,

    @Schema(description = "등록할 파일", required = false)
    val uploadFileResponse: MutableList<UploadFileResponse>?
)

package com.tobe.healthy.lessonhistory.domain.dto.`in`

import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "댓글 등록 DTO")
data class CommandRegisterComment(
    @Schema(description = "등록할 댓글 내용")
    @field:NotBlank(message = "내용을 입력해 주세요.")
    val comment: String?,

    @Schema(description = "등록할 파일", required = false)
    val commandUploadFileResult: MutableList<CommandUploadFileResult>?
)

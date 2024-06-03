package com.tobe.healthy.lessonhistory.domain.dto.`in`

import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "수업 일지 댓글 수정 DTO")
data class CommandUpdateComment(
    @Schema(description = "수정할 댓글 내용", example = "트레이너님 휴대폰 그만봐", required = true)
    val content: String,

    @Schema(description = "등록할 파일", required = false)
    val uploadFiles: MutableList<CommandUploadFileResult> = mutableListOf()
)

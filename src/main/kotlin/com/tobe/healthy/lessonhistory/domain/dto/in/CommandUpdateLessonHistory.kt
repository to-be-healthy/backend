package com.tobe.healthy.lessonhistory.domain.dto.`in`

import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult
import io.swagger.v3.oas.annotations.media.Schema

data class CommandUpdateLessonHistory(
    @Schema(description = "수정할 수업일지 제목")
    val title: String,

    @Schema(description = "수정할 수업일지 내용")
    val content: String,

    @Schema(description = "등록할 파일", required = false)
    val uploadFiles: MutableList<CommandUploadFileResult> = mutableListOf()
)

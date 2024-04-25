package com.tobe.healthy.lessonHistory.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema

data class LessonHistoryCommand(
    @Schema(description = "수정할 수업일지 제목")
    val title: String,
    @Schema(description = "수정할 수업일지 내용")
    val content: String
)

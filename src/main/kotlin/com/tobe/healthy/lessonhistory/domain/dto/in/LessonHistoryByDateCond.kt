package com.tobe.healthy.lessonhistory.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "조회 조건 DTO")
data class LessonHistoryByDateCond(
    @Schema(description = "조회 날짜", example = "YYYY-MM", required = false)
    val searchDate: String? = null,
    @Schema(description = "내 수업 일지 조회 여부", example = "Y | N", required = false)
    val searchMyHistory: String? = null
)

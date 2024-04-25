package com.tobe.healthy.lessonHistory.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "조회 조건 DTO")
data class SearchCondRequest(
    @Schema(description = "조회 날짜", example = "YYYYMM", required = false)
    val searchDate: String? = null,
    @Schema(description = "내 수업 일지 조회 여부", example = "Y | N", required = false)
    val searchMyHistory: String? = null
)

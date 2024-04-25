package com.tobe.healthy.lessonHistory.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema

data class SearchCondRequest(
    @Schema(description = "조회 날짜", example = "YYYYMM")
    val searchDate: String? = null,
    val searchMyHistory: String? = null
)

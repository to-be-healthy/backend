package com.tobe.healthy.lessonHistory.domain.dto

data class SearchCondRequest(
    val searchDate: String? = null,
    val searchMyHistory: String? = null
)

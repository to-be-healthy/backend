package com.tobe.healthy.common

data class CustomPagingResponse<T>(
    val studentName: String? = null,
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val isLast: Boolean
)

package com.tobe.healthy.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL

data class CustomPagingResponse<T>(
    @JsonInclude(NON_NULL)
    val studentName: String? = null,
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val isLast: Boolean
)

package com.tobe.healthy.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.tobe.healthy.common.NotificationSenderInfo.SenderInfo
import com.tobe.healthy.notification.domain.dto.out.NotificationRedDotStatusResult

data class KotlinCustomPaging<T>(
    var content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val isLast: Boolean,
    @JsonInclude(NON_EMPTY)
    val redDotStatus: List<NotificationRedDotStatusResult>? = mutableListOf(),
    @JsonInclude(NON_NULL)
    val sender: SenderInfo? = null,
)

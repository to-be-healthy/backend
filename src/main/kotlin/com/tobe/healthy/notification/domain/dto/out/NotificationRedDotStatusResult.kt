package com.tobe.healthy.notification.domain.dto.out

import com.querydsl.core.annotations.QueryProjection
import com.tobe.healthy.notification.domain.entity.NotificationCategory

data class NotificationRedDotStatusResult @QueryProjection constructor(
    val notificationCategory: NotificationCategory,
    val redDotStatus: Boolean
)

package com.tobe.healthy.notification.domain.dto.out

import com.querydsl.core.annotations.QueryProjection
import com.tobe.healthy.notification.domain.entity.NotificationType

data class NotificationRedDotStatusResult @QueryProjection constructor(
    val notificationType: NotificationType,
    val redDotStatus: Boolean
)

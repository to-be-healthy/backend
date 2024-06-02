package com.tobe.healthy.notification.domain.dto.`in`

import com.tobe.healthy.notification.domain.entity.NotificationType

data class RetrieveNotification(
    val notificationType: List<NotificationType>
)

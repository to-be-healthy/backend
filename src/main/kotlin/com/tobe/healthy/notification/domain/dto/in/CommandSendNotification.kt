package com.tobe.healthy.notification.domain.dto.`in`

import com.tobe.healthy.notification.domain.entity.NotificationType

data class CommandSendNotification(
    val title: String,
    val content: String,
    val receiverIds: List<Long>,
    val notificationType: NotificationType
)

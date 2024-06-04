package com.tobe.healthy.notification.domain.dto.out

import com.tobe.healthy.notification.domain.entity.Notification

data class CommandNotificationStatusResult(
    val notificationId: Long?,
    val isRead: Boolean
) {
    companion object {
        fun from(notification: Notification) : CommandNotificationStatusResult {
            return CommandNotificationStatusResult(
                notificationId = notification.id,
                isRead = notification.isRead
            )
        }
    }
}

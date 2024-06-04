package com.tobe.healthy.notification.domain.dto.out

import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationCategory
import com.tobe.healthy.notification.domain.entity.NotificationType

data class RetrieveNotificationDetailResult(
    val notificationId: Long?,
    val senderId: Long,
    val senderName: String,
    val senderProfile: String?,
    val title: String,
    val content: String,
    val notificationCategory: NotificationCategory,
    val notificationType: NotificationType,
    val createdAt: String
) {
    companion object {
        fun from(notification: Notification): RetrieveNotificationDetailResult {
            return RetrieveNotificationDetailResult(
                notificationId = notification.id,
                senderId = notification.sender.id,
                senderName = notification.sender.name,
                senderProfile = notification.sender.memberProfile?.fileUrl,
                title = notification.title,
                content = notification.content,
                notificationCategory = notification.notificationCategory,
                notificationType = notification.notificationType,
                createdAt = notification.createdAt.toString()
            )
        }
    }
}

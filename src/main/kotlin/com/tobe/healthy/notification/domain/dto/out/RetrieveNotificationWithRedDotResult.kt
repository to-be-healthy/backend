package com.tobe.healthy.notification.domain.dto.out

import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationType
import org.springframework.data.domain.Slice

data class RetrieveNotificationWithRedDotResult(
    val notificationResult: Slice<RetrieveNotificationResult>,
    val redDotStatus: List<NotificationRedDotStatusResult>
) {
    companion object {
        fun from(
            notifications: Slice<Notification>,
            redDotStatus: List<NotificationRedDotStatusResult>
        ) : RetrieveNotificationWithRedDotResult {
            return RetrieveNotificationWithRedDotResult(
                notificationResult = notifications.map { RetrieveNotificationResult.from(it) },
                redDotStatus = redDotStatus
            )
        }
    }

    data class RetrieveNotificationResult(
        val notificationId: Long?,
        val senderId: Long,
        val senderName: String,
        val senderProfile: String?,
        val title: String,
        val content: String,
        val notificationType: NotificationType,
        val createdAt: String,
        val isRead: Boolean
    ) {
        companion object {
            fun from(notification: Notification) : RetrieveNotificationResult {
                return RetrieveNotificationResult(
                    notificationId = notification.id,
                    senderId = notification.sender.id,
                    senderName = notification.sender.name,
                    senderProfile = notification.sender.memberProfile?.fileUrl,
                    title = notification.title,
                    content = notification.content,
                    notificationType = notification.notificationType,
                    createdAt = notification.createdAt.toString(),
                    isRead = notification.isRead
                )
            }
        }
    }
}

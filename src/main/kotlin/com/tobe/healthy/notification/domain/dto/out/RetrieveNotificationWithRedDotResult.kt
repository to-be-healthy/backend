package com.tobe.healthy.notification.domain.dto.out

import com.tobe.healthy.notification.domain.entity.Notification
import org.springframework.data.domain.Page

data class RetrieveNotificationWithRedDotResult(
    val content: List<RetrieveNotificationResult>,
    val redDotStatus: List<NotificationRedDotStatusResult>
) {
    companion object {
        fun from(
            notifications: Page<Notification>,
            redDotStatus: List<NotificationRedDotStatusResult>
        ) : RetrieveNotificationWithRedDotResult {
            return RetrieveNotificationWithRedDotResult(
                content = notifications.content.map { RetrieveNotificationResult.from(it) },
                redDotStatus = redDotStatus
            )
        }
    }

    data class RetrieveNotificationResult(
        val notificationId: Long?,
        val notificationCategoryAndType: String,
        val senderId: Long,
        val senderName: String,
        val senderProfile: String?,
        val title: String,
        val content: String,
        val createdAt: String,
        val isRead: Boolean,
        val lessonHistoryId: Long? = null
    ) {
        companion object {
            fun from(notification: Notification) : RetrieveNotificationResult {
                return RetrieveNotificationResult(
                    notificationId = notification.id,
                    notificationCategoryAndType = "${notification.notificationCategory.name + "-" + notification.notificationType.name}",
                    senderId = notification.sender.id,
                    senderName = notification.sender.name,
                    senderProfile = notification.sender.memberProfile?.fileUrl,
                    title = notification.title,
                    content = notification.content,
                    createdAt = notification.createdAt.toString(),
                    isRead = notification.isRead,
                    lessonHistoryId = notification.lessonHistory?.id
                )
            }
        }
    }
}

package com.tobe.healthy.notification.domain.dto.out

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.notification.domain.entity.Notification

data class CommandSendNotificationResult(
    val notificationId: Long?,
    val title: String?,
    val content: String?,
    val senderId: Long?,
    val senderName: String?,
    val receivers: List<NotificationReciverInfo>,
) {
    companion object {
        fun from(notifications: MutableList<Notification>): CommandSendNotificationResult {
            return CommandSendNotificationResult(
                notificationId = notifications.firstOrNull()?.id,
                title = notifications.firstOrNull()?.title,
                content = notifications.firstOrNull()?.content,
                senderId = notifications.firstOrNull()?.sender?.id,
                senderName = notifications.firstOrNull()?.sender?.name,
                receivers = notifications.map { NotificationReciverInfo.from(it.receiver) }
            )
        }
    }

    data class NotificationReciverInfo(
        val receiverId: Long?,
        val receiverName: String?
    ) {
        companion object {
            fun from(receiver: Member?) : NotificationReciverInfo {
                return NotificationReciverInfo(
                    receiverId = receiver?.id,
                    receiverName = receiver?.name
                )
            }
        }
    }
}

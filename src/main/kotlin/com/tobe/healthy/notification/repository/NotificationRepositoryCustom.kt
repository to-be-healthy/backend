package com.tobe.healthy.notification.repository

import com.tobe.healthy.notification.domain.dto.out.NotificationRedDotStatusResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationType

interface NotificationRepositoryCustom {
    fun findAllByNotificationType(notificationType: List<NotificationType>?, receiverId: Long): List<Notification>
    fun findOneById(notificationId: Long, receiverId: Long): Notification?
    fun findAllRedDotStatus(notificationType: List<NotificationType>?, receiverId: Long): List<NotificationRedDotStatusResult>
}
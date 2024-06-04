package com.tobe.healthy.notification.repository

import com.tobe.healthy.notification.domain.dto.out.NotificationRedDotStatusResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface NotificationRepositoryCustom {
    fun findAllByNotificationType(notificationType: NotificationType, receiverId: Long, pageable: Pageable): Slice<Notification>
    fun findOneById(notificationId: Long, receiverId: Long): Notification?
    fun findAllRedDotStatus(notificationType: NotificationType, receiverId: Long): List<NotificationRedDotStatusResult>
    fun findRedDotStatus(receiverId: Long) : Boolean
}
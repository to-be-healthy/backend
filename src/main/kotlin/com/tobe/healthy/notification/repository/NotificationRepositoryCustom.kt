package com.tobe.healthy.notification.repository

import com.tobe.healthy.notification.domain.dto.out.NotificationRedDotStatusResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationRepositoryCustom {
    fun findAllByNotificationType(notificationCategory: NotificationCategory, receiverId: Long, pageable: Pageable): Page<Notification>
    fun findAllRedDotStatus(notificationCategory: NotificationCategory, receiverId: Long): List<NotificationRedDotStatusResult>
    fun findRedDotStatus(receiverId: Long) : Boolean
}
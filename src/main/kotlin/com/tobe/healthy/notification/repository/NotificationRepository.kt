package com.tobe.healthy.notification.repository

import com.tobe.healthy.notification.domain.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    fun findByIdAndReceiverId(notificationId: Long, receiverId: Long): Notification?
}
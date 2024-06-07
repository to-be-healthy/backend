package com.tobe.healthy.notification.repository

import com.tobe.healthy.notification.domain.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface NotificationRepository : JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    fun findByIdAndReceiverId(notificationId: Long, receiverId: Long): Notification?

    @Modifying
    @Query("UPDATE Notification n SET n.lessonHistory = null WHERE n.lessonHistory.id = :lessonHistoryId")
    fun nullifyLessonHistoryId(lessonHistoryId: Long)
}
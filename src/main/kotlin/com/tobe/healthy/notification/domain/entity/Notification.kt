package com.tobe.healthy.notification.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.notification.domain.entity.NotificationSenderType.SYSTEM
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
class Notification(

    val title: String,

    val content: String,

    @Enumerated(STRING)
    val notificationCategory: NotificationCategory,

    @Enumerated(STRING)
    val notificationType: NotificationType,

    @Enumerated(STRING)
    val senderType: NotificationSenderType = SYSTEM,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receiver_id")
    val receiver: Member? = null,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_id")
    val lessonHistory: LessonHistory? = null,

    var isRead: Boolean = false,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "notification_id")
    val id: Long? = null

) : BaseTimeEntity<Notification, Long>() {

    fun updateNotificationStatus() {
        this.isRead = true
    }

    companion object {
        fun create(
            title: String,
            content: String,
            notificationCategory: NotificationCategory,
            notificationType: NotificationType,
            receiver: Member,
            lessonHistory: LessonHistory? = null
        ): Notification {
            return Notification(
                title = title,
                content = content,
                notificationCategory = notificationCategory,
                notificationType = notificationType,
                receiver = receiver,
                lessonHistory = lessonHistory
            )
        }
    }
}
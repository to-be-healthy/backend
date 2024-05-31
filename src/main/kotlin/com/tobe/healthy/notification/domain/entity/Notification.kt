package com.tobe.healthy.notification.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY

@Entity
class Notification(
    val title: String,

    val content: String,

    @Enumerated(STRING)
    val notificationType: NotificationType,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    val sender: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receiver_id")
    val receiver: Member,

    var isRead: Boolean = false,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "notification_id")
    val id: Long? = null
) : BaseTimeEntity<Notification, Long>() {

    fun readNotification() {
        this.isRead = true
    }

    companion object {
        fun create(title: String, content: String, notificationType: NotificationType, sender: Member, receiver: Member): Notification {
            return Notification(
                title = title,
                content = content,
                notificationType = notificationType,
                sender = sender,
                receiver = receiver
            )
        }
    }
}
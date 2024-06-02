package com.tobe.healthy.notification.repository

import com.querydsl.core.types.Projections.constructor
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.notification.domain.dto.out.NotificationRedDotStatusResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationType
import com.tobe.healthy.notification.domain.entity.QNotification.notification
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils

@Repository
class NotificationRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : NotificationRepositoryCustom {

    override fun findAllByNotificationType(
        notificationType: List<NotificationType>?,
        receiverId: Long
    ): List<Notification> {
        return queryFactory
            .select(notification)
            .from(notification)
            .innerJoin(notification.sender).fetchJoin()
            .where(
                notificationTypeIn(notificationType),
                notification.receiver.id.eq(receiverId)
            )
            .orderBy(notification.id.desc())
            .fetch()
    }

    override fun findOneById(notificationId: Long, receiverId: Long): Notification? {
        return queryFactory
            .select(notification)
            .from(notification)
            .where(
                notification.id.eq(notificationId),
                notification.receiver.id.eq(receiverId)
            )
            .fetchOne()
    }

    override fun findAllRedDotStatus(notificationType: List<NotificationType>?, receiverId: Long): List<NotificationRedDotStatusResult> {
        return queryFactory
            .select(
                constructor(
                    NotificationRedDotStatusResult::class.java,
                    notification.notificationType,
                    notification.count().gt(0)
                )
            )
            .from(notification)
            .where(
                notificationTypeNotIn(notificationType),
                notification.receiver.id.eq(receiverId),
                notification.isRead.eq(false)
            )
            .groupBy(notification.notificationType)
            .fetch()
    }

    private fun notificationTypeIn(notificationType: List<NotificationType>?): BooleanExpression? {
        if (ObjectUtils.isEmpty(notificationType)) {
            return null
        }
        return notification.notificationType.`in`(notificationType)
    }

    private fun notificationTypeNotIn(notificationType: List<NotificationType>?): BooleanExpression? {
        if (ObjectUtils.isEmpty(notificationType)) {
            return null
        }
        return notification.notificationType.notIn(notificationType)
    }
}

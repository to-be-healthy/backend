package com.tobe.healthy.notification.repository

import com.querydsl.core.types.Projections.constructor
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.notification.domain.dto.out.NotificationRedDotStatusResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationType
import com.tobe.healthy.notification.domain.entity.QNotification.notification
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils


@Repository
class NotificationRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : NotificationRepositoryCustom {

    override fun findAllByNotificationType(
        notificationType: NotificationType,
        receiverId: Long,
        pageable: Pageable
    ): Slice<Notification> {
        val contents = queryFactory
            .select(notification)
            .from(notification)
            .innerJoin(notification.sender).fetchJoin()
            .where(
                notificationTypeEq(notificationType),
                notification.receiver.id.eq(receiverId)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(notification.id.desc())
            .fetch()

        return if (hasNext(contents.size, pageable.pageSize)) {
            SliceImpl(contents.subList(0, pageable.pageSize), pageable, hasNext(contents.size, pageable.pageSize))
        } else {
            SliceImpl(contents, pageable, hasNext(contents.size, pageable.pageSize))
        }
    }

    private fun hasNext(contentSize: Int, pageSize: Int): Boolean {
        return contentSize > pageSize
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

    override fun findAllRedDotStatus(notificationType: NotificationType, receiverId: Long): List<NotificationRedDotStatusResult> {
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
                notificationTypeNq(notificationType),
                notification.receiver.id.eq(receiverId),
                notification.isRead.eq(false)
            )
            .groupBy(notification.notificationType)
            .fetch()
    }

    override fun findRedDotStatus(receiverId: Long): Boolean {
        val count = queryFactory
            .select(notification.count())
            .from(notification)
            .where(
                notification.receiver.id.eq(receiverId),
                notification.isRead.eq(false)
            )
            .fetchOne() ?: 0
        return count > 0
    }

    private fun notificationTypeEq(notificationType: NotificationType): BooleanExpression? {
        if (ObjectUtils.isEmpty(notificationType)) {
            return null
        }
        return notification.notificationType.`in`(notificationType)
    }

    private fun notificationTypeNq(notificationType: NotificationType): BooleanExpression? {
        if (ObjectUtils.isEmpty(notificationType)) {
            return null
        }
        return notification.notificationType.`in`(notificationType)
    }
}

package com.tobe.healthy.notification.repository

import com.querydsl.core.types.Projections.constructor
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.notification.domain.dto.out.NotificationRedDotStatusResult
import com.tobe.healthy.notification.domain.entity.Notification
import com.tobe.healthy.notification.domain.entity.NotificationCategory
import com.tobe.healthy.notification.domain.entity.QNotification.notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils


@Repository
class NotificationRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : NotificationRepositoryCustom {

    override fun findAllByNotificationType(
        notificationCategory: NotificationCategory,
        receiverId: Long,
        pageable: Pageable
    ): Page<Notification> {

        val results = queryFactory
            .select(notification)
            .from(notification)
            .where(
                notificationCategoryEq(notificationCategory),
                notification.receiver.id.eq(receiverId)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(notification.id.desc())
            .fetch()

        val totalCount = queryFactory
            .select(notification.count())
            .from(notification)
            .where(
                notificationCategoryEq(notificationCategory),
                notification.receiver.id.eq(receiverId)
            )

        return PageableExecutionUtils.getPage(results, pageable) { totalCount.fetchOne() ?: 0L }
    }

    override fun findAllRedDotStatus(
        notificationCategory: NotificationCategory,
        receiverId: Long
    ): List<NotificationRedDotStatusResult> {

        return queryFactory
            .select(
                constructor(
                    NotificationRedDotStatusResult::class.java,
                    notification.notificationCategory,
                    CaseBuilder()
                        .`when`(CaseBuilder()
                                .`when`(notification.receiver.id.eq(receiverId).and(notification.isRead.eq(false)))
                                .then(1)
                                .otherwise(0)
                                .sum().gt(0)
                        )
                        .then(true)
                        .otherwise(false)
                )
            )
            .from(notification)
            .where(
                notificationCategoryNq(notificationCategory),
            )
            .groupBy(notification.notificationCategory)
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

    private fun notificationCategoryEq(notificationCategory: NotificationCategory): BooleanExpression? {
        if (ObjectUtils.isEmpty(notificationCategory)) {
            return null
        }
        return notification.notificationCategory.eq(notificationCategory)
    }

    private fun notificationCategoryNq(notificationCategory: NotificationCategory): BooleanExpression? {
        if (ObjectUtils.isEmpty(notificationCategory)) {
            return null
        }
        return notification.notificationCategory.ne(notificationCategory)
    }
}

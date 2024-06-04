package com.tobe.healthy.notification.domain.entity

import com.tobe.healthy.notification.domain.entity.NotificationCategory.COMMUNITY
import com.tobe.healthy.notification.domain.entity.NotificationCategory.SCHEDULE

enum class NotificationType(
    val category: NotificationCategory,
    val description: String
) {
    FEEDBACK(SCHEDULE,"피드백"),
    RESERVE(SCHEDULE,"예약완료"),
    CANCEL(SCHEDULE,"예약취소"),
    COMMENT(COMMUNITY,"댓글"),
    REPLY(COMMUNITY,"답글");
}
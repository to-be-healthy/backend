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
    COMMENT(COMMUNITY,"게시글 댓글"),
    WRITE(COMMUNITY,"게시글 작성"),
    REPLY(COMMUNITY,"댓글 답글"),
    WAITING(SCHEDULE, "대기 예약 확정");
}
package com.tobe.healthy.notification.domain.entity

enum class NotificationSenderType(
    val description: String
) {
    SYSTEM("시스템"),
    USER("사용자")
}
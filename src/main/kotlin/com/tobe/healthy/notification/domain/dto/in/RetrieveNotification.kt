package com.tobe.healthy.notification.domain.dto.`in`

import com.tobe.healthy.notification.domain.entity.NotificationType
import jakarta.validation.constraints.NotNull

data class RetrieveNotification(
    @field:NotNull(message = "알림 타입을 입력해 주세요.")
    val notificationType: List<NotificationType>?
)

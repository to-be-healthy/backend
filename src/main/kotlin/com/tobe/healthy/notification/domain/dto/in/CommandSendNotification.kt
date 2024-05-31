package com.tobe.healthy.notification.domain.dto.`in`

import com.tobe.healthy.notification.domain.entity.NotificationType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CommandSendNotification(
    @field:NotBlank(message = "제목을 입력해 주세요.")
    val title: String?,

    @field:NotBlank(message = "내용을 입력해 주세요.")
    val content: String?,

    @field:NotNull(message = "수신자를 입력해 주세요.")
    val receiverIds: List<Long>?,

    @field:NotNull(message = "알림 타입을 입력해 주세요.")
    val notificationType: NotificationType?
)

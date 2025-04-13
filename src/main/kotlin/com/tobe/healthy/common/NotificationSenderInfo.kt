package com.tobe.healthy.common

import com.tobe.healthy.notification.domain.entity.NotificationSenderType
import com.tobe.healthy.notification.domain.entity.NotificationSenderType.SYSTEM

object NotificationSenderInfo {
    fun getSenderInfo() : SenderInfo {
        return SenderInfo()
    }

    data class SenderInfo(
        val profileUrl: String = "https://cdn.to-be-healthy.shop/origin/profile/default.png",
        val senderType: NotificationSenderType = SYSTEM
    )
}
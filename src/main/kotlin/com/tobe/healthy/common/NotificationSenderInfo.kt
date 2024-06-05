package com.tobe.healthy.common

import com.tobe.healthy.notification.domain.entity.NotificationSenderType
import com.tobe.healthy.notification.domain.entity.NotificationSenderType.SYSTEM

object NotificationSenderInfo {
    fun getSenderInfo() : SenderInfo {
        return SenderInfo()
    }

    data class SenderInfo(
        val profileUrl: String = "https://to-be-healthy-bucket.s3.ap-northeast-2.amazonaws.com/profile/default.png",
        val senderType: NotificationSenderType = SYSTEM
    )
}
package com.tobe.healthy.push.domain.dto.`in`

import com.tobe.healthy.push.domain.entity.DeviceType

data class CommandSendPushAlarm(
    val title: String,
    val message: String,
    val token: String,
    val clickUrl: String? = null,
    val deviceType: DeviceType
)

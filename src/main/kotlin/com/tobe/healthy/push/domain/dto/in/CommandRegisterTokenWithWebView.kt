package com.tobe.healthy.push.domain.dto.`in`

import com.tobe.healthy.push.domain.entity.DeviceType

data class CommandRegisterTokenWithWebView(
    val memberId: Long,
    val token: String,
    val deviceType: DeviceType
)

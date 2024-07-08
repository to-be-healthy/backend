package com.tobe.healthy.push.domain.dto.`in`

data class CommandRegisterTokenWithWebView(
    val memberId: Long,
    val token: String,
    val deviceType: String
)

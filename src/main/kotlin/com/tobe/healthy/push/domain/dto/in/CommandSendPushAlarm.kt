package com.tobe.healthy.push.domain.dto.`in`

data class CommandSendPushAlarm(
    val title: String,
    val message: String,
    val token: String,
    val clickUrl: String? = null
)

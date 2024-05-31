package com.tobe.healthy.push.domain.dto.out

data class CommandSendPushAlarmResult(
    val title: String,
    val message: String
) {
    companion object {
        fun from(title: String, message: String): CommandSendPushAlarmResult {
            return CommandSendPushAlarmResult(
                title = title,
                message = message
            )
        }
    }
}

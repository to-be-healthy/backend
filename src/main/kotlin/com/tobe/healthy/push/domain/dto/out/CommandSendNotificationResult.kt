package com.tobe.healthy.push.domain.dto.out

data class CommandSendNotificationResult(
    val title: String,
    val message: String
) {
    companion object {
        fun from(title: String, message: String): CommandSendNotificationResult {
            return CommandSendNotificationResult(
                title = title,
                message = message
            )
        }
    }
}

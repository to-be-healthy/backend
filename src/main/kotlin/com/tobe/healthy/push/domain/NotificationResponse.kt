package com.tobe.healthy.push.domain

data class NotificationResponse(
    val title: String,
    val message: String
) {
    companion object {
        fun from(title: String, message: String): NotificationResponse {
            return NotificationResponse(
                title = title,
                message = message
            )
        }
    }
}

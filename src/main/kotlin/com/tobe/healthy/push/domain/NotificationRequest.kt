package com.tobe.healthy.push.domain

data class NotificationRequest(
    val title: String,
    val message: String,
    // todo: 2024-05-07 화요일 오후 14:31 다른 조건으로 사용자를 구분하는 기능이 필요(사용자 이름 또는 id등으로) - seonwoo_jung
    val token: String
)
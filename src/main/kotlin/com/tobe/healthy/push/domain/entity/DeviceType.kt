package com.tobe.healthy.push.domain.entity

enum class DeviceType(
    val description: String
) {
    WEB("웹"),
    AOS("안드로이드"),
    IOS("애플")
}
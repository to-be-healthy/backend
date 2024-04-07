package com.tobe.healthy.lessonHistory.domain.entity

enum class AttendanceStatus(
    val description: String
) {
    ATTENDED("출석"),
    ABSENT("미출석");
}
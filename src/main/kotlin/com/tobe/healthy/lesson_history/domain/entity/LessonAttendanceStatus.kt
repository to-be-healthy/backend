package com.tobe.healthy.lesson_history.domain.entity

enum class AttendanceStatus(
    val description: String
) {
    ATTENDED("출석"),
    ABSENT("미출석");
}

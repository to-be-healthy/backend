package com.tobe.healthy.lessonHistory.domain.dto

data class RegisterLessonHistoryCommand(
    val title: String,
    val content: String,
    val trainer: Long,
    val schedule: Long
)

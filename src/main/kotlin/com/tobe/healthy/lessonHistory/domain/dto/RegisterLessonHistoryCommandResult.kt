package com.tobe.healthy.lessonHistory.domain.dto

data class RegisterLessonHistoryCommandResult(
    val title: String,
    val content: String,
    val trainer: Long,
    val schedule: Long
)

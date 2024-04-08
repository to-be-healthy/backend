package com.tobe.healthy.lessonHistory.domain.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class SearchCondRequest(
    val searchDate: String
)

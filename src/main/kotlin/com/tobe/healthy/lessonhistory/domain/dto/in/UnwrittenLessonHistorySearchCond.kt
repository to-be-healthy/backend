package com.tobe.healthy.lessonhistory.domain.dto.`in`

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale.KOREAN

data class UnwrittenLessonHistorySearchCond(
    val lessonDateTime: String? = formatDate(LocalDateTime.now())
) {
    companion object {
        private fun formatDate(lessonDateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD", KOREAN)
            return lessonDateTime.format(formatter)
        }
    }
}

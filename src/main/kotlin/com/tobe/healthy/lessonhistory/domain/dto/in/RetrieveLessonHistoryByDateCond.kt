package com.tobe.healthy.lessonhistory.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.format.DateTimeFormatter
import java.util.Locale.KOREAN

@Schema(description = "조회 조건 DTO")
data class RetrieveLessonHistoryByDateCond(
    @Schema(description = "조회 날짜", example = "YYYY-MM", required = false)
    val searchDate: String = formatDate(now())
) {
    companion object {
        private fun formatDate(lessonDt: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("YYYY-MM", KOREAN)
            return lessonDt.format(formatter)
        }
    }
}
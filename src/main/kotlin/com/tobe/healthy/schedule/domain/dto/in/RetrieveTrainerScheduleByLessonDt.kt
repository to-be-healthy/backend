package com.tobe.healthy.schedule.domain.dto.`in`

import io.swagger.v3.oas.annotations.media.Schema

data class RetrieveTrainerScheduleByLessonDt(
    @Schema(description = "조회할 수업 일자", example = "2024-04-01")
    val lessonDt: String,
) {
    companion object {
        @JvmStatic
        fun of(lessonDt: String): RetrieveTrainerScheduleByLessonDt {
            return RetrieveTrainerScheduleByLessonDt(
                lessonDt = lessonDt
            )
        }
    }
}

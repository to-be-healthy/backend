package com.tobe.healthy.schedule.entity.`in`

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.INVALID_LESSON_TIME_DESCRIPTION
import com.tobe.healthy.config.error.ErrorCode.START_TIME_AFTER_END_TIME
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class CommandRegisterIndividualSchedule(
    @Schema(description = "등록할 수업 일자", example = "2024-04-01")
    val lessonDt: LocalDate,

    @Schema(description = "등록할 수업 시작 시간", example = "10:00:00", type = "string")
    val lessonStartTime: LocalTime,

    @Schema(description = "등록할 수업 종료 시간", example = "11:00:00", type = "string")
    val lessonEndTime: LocalTime
) {
    init {
        if (!lessonEndTime.isAfter(lessonStartTime)) {
            throw CustomException(START_TIME_AFTER_END_TIME)
        }
        if (Duration.between(lessonStartTime, lessonEndTime).toMinutes() % 30 != 0L) {
            throw CustomException(INVALID_LESSON_TIME_DESCRIPTION)
        }
    }
}

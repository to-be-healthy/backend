package com.tobe.healthy.schedule.entity.`in`

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.LUNCH_TIME_INVALID
import com.tobe.healthy.config.error.ErrorCode.START_TIME_AFTER_END_TIME
import com.tobe.healthy.schedule.domain.entity.LessonTime
import io.swagger.v3.oas.annotations.media.Schema
import java.time.DayOfWeek
import java.time.LocalTime

data class RegisterDefaultLessonTimeRequest(
    @Schema(description = "시작 수업 시간", example = "10:00:00", type = "string")
    val startTime: LocalTime,

    @Schema(description = "종료 수업 시간", example = "22:00:00", type = "string")
    val endTime: LocalTime,

    @Schema(description = "시작 점심시간", example = "12:00:00", type = "string")
    val lunchStartTime: LocalTime? = null,

    @Schema(description = "종료 점심시간", example = "13:00:00", type = "string")
    val lunchEndTime: LocalTime? = null,

    val closedDt: List<DayOfWeek>? = null,

    @Schema(description = "세션당 수업 시간", example = "HALF_HOUR | ONE_HOUR | ONE_AND_HALF_HOUR | TWO_HOUR")
    val sessionTime: LessonTime
) {
    init {
        if (!startTime.isBefore(endTime)) {
            throw CustomException(LUNCH_TIME_INVALID)
        }
        if (lunchStartTime?.isBefore(lunchEndTime) == false) {
            throw CustomException(START_TIME_AFTER_END_TIME)
        }
    }
}

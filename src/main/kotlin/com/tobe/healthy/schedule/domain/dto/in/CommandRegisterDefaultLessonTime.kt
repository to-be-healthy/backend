package com.tobe.healthy.schedule.domain.dto.`in`

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.LUNCH_TIME_INVALID
import com.tobe.healthy.config.error.ErrorCode.START_TIME_AFTER_END_TIME
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.DayOfWeek
import java.time.LocalTime

data class CommandRegisterDefaultLessonTime(
    @Schema(description = "시작 수업 시간", example = "10:00:00", type = "string")
    val lessonStartTime: LocalTime = LocalTime.of(9, 0, 0),

    @Schema(description = "종료 수업 시간", example = "22:00:00", type = "string")
    val lessonEndTime: LocalTime = LocalTime.of(18, 0, 0),

    @Schema(description = "시작 점심시간", example = "12:00:00", type = "string")
    val lunchStartTime: LocalTime? = null,

    @Schema(description = "종료 점심시간", example = "13:00:00", type = "string")
    val lunchEndTime: LocalTime? = null,

    val closedDays: List<DayOfWeek>? = mutableListOf(),

    @Schema(description = "세션당 수업 시간", example = "30|60|90|120")
    @field:NotNull(message = "수업 시간을 입력해 주세요.")
    val lessonTime: Int?
) {
    init {
        if (lessonStartTime.isAfter(lessonEndTime) || lessonStartTime == lessonEndTime) {
            throw CustomException(START_TIME_AFTER_END_TIME)
        }
        if (lunchStartTime?.isAfter(lunchEndTime) == true) {
            throw CustomException(LUNCH_TIME_INVALID)
        }
    }
}

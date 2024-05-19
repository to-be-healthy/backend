package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.common.TimeFormatter.Companion.dateTimeFormat
import com.tobe.healthy.schedule.entity.`in`.CommandRegisterDefaultLessonTime
import java.time.DayOfWeek

data class CommandRegisterDefaultLessonTimeResult(
    val lessonStartTime: String?,
    val lessonEndTime: String?,
    val lunchStartTime: String? = null,
    val lunchEndTime: String? = null,
    val closedDays: List<DayOfWeek>? = null,
    val lessonTime: Int
) {
    companion object {
        fun from(request: CommandRegisterDefaultLessonTime): CommandRegisterDefaultLessonTimeResult {
            return CommandRegisterDefaultLessonTimeResult(
                lessonStartTime = dateTimeFormat(request.lessonStartTime),
                lessonEndTime = dateTimeFormat(request.lessonEndTime),
                lunchStartTime = dateTimeFormat(request.lunchStartTime),
                lunchEndTime = dateTimeFormat(request.lunchEndTime),
                closedDays = request.closedDays,
                lessonTime = request.lessonTime
            )
        }
    }
}

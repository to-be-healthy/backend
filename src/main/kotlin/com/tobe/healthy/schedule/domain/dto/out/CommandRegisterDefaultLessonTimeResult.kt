package com.tobe.healthy.schedule.domain.dto.out

import com.tobe.healthy.common.LessonTimeFormatter.formatLessonTime
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterDefaultLessonTime
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
                lessonStartTime = formatLessonTime(request.lessonStartTime),
                lessonEndTime = formatLessonTime(request.lessonEndTime),
                lunchStartTime = formatLessonTime(request.lunchStartTime),
                lunchEndTime = formatLessonTime(request.lunchEndTime),
                closedDays = request.closedDays,
                lessonTime = request.lessonTime!!
            )
        }
    }
}

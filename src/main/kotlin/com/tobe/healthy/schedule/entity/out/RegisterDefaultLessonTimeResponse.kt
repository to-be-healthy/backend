package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.common.TimeFormatter.Companion.dateTimeFormat
import com.tobe.healthy.schedule.entity.`in`.RegisterDefaultLessonTimeRequest
import java.time.DayOfWeek

data class RegisterDefaultLessonTimeResponse(
    val startTime: String?,
    val endTime: String?,
    val lunchStartTime: String? = null,
    val lunchEndTime: String? = null,
    val closedDt: List<DayOfWeek>? = null,
    val sessionTime: Int
) {
    companion object {
        fun from(request: RegisterDefaultLessonTimeRequest): RegisterDefaultLessonTimeResponse {
            return RegisterDefaultLessonTimeResponse(
                startTime = dateTimeFormat(request.startTime),
                endTime = dateTimeFormat(request.endTime),
                lunchStartTime = dateTimeFormat(request.lunchStartTime),
                lunchEndTime = dateTimeFormat(request.lunchEndTime),
                closedDt = request.closedDt,
                sessionTime = request.sessionTime
            )
        }
    }
}

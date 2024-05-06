package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.schedule.domain.entity.LessonTime
import com.tobe.healthy.schedule.entity.`in`.RegisterDefaultLessonTimeRequest
import java.time.DayOfWeek
import java.time.LocalTime

data class RegisterDefaultLessonTimeResponse(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val lunchStartTime: LocalTime? = null,
    val lunchEndTime: LocalTime? = null,
    val closedDt: List<DayOfWeek>? = null,
    val sessionTime: LessonTime
) {
    companion object {
        fun from(request: RegisterDefaultLessonTimeRequest): RegisterDefaultLessonTimeResponse {
            return RegisterDefaultLessonTimeResponse(
                startTime = request.startTime,
                endTime = request.endTime,
                lunchStartTime = request.lunchStartTime,
                lunchEndTime = request.lunchEndTime,
                closedDt = request.closedDt,
                sessionTime = request.sessionTime
            )
        }
    }
}

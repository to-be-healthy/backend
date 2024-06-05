package com.tobe.healthy.common

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

object LessonTimeFormatter {
    fun formatLessonTime(lessonStartTime: LocalTime?, lessonEndTime: LocalTime?): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val startTime = lessonStartTime?.format(formatter)
        val endTime = lessonEndTime?.format(formatter)
        return "${startTime} - ${endTime}"
    }

    fun formatLessonTimeWithAMPM(lessonStartTime: LocalTime, lessonEndTime: LocalTime): String {
        val startTimeFormatter = DateTimeFormatter.ofPattern("a hh:mm")
        val endTimeFormatter = DateTimeFormatter.ofPattern("hh:mm")
        val startTime = lessonStartTime.format(startTimeFormatter)
        val endTime = lessonEndTime.format(endTimeFormatter)
        return "${startTime} - ${endTime}"
    }

    fun formatLessonTime(localTime: LocalTime?): String? {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return localTime?.format(formatter)
    }

    fun formatLessonDt(lessonDt: LocalDate?): String? {
        val formatter = DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN)
        return lessonDt?.format(formatter)
    }

    @JvmStatic
    fun lessonStartDateTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("M월 d일(E) h시")
    }
}
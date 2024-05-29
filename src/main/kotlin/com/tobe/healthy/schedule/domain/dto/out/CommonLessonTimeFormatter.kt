package com.tobe.healthy.schedule.domain.dto.out

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class CommonLessonTimeFormatter {

}

fun formatLessonTime(lessonStartTime: LocalTime, lessonEndTime: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = lessonStartTime.format(formatter)
    val endTime = lessonEndTime.format(formatter)
    return "${startTime} - ${endTime}"
}

fun formatLessonDt(lessonDt: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN)
    return lessonDt.format(formatter)
}

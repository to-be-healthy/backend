package com.tobe.healthy.common

import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeFormatter() {

    companion object {
        fun dateTimeFormat(localTime: LocalTime?): String? {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            return localTime?.format(formatter)
        }
    }
}
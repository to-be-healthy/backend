package com.tobe.healthy.common

import com.tobe.healthy.log
import io.kotest.core.spec.style.StringSpec
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale.KOREAN

class TimeFormatterTest : StringSpec({

    "Time Formatter를 테스트한다" {
        val formatter = DateTimeFormatter.ofPattern("YYYY-MM", KOREAN)
        val format = LocalDate.now().format(formatter)
        log.info { "format: $format"}
    }
})

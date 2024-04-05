package com.tobe.healthy.schedule.application

import com.tobe.healthy.log
import com.tobe.healthy.schedule.domain.dto.`in`.AutoCreateScheduleCommand
import com.tobe.healthy.schedule.repository.ScheduleRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
class KotlinScheduleServiceTest @Autowired constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleService: ScheduleService
) {

    @Test
    fun `자동으로 일정을 생성한다`() {
        val request: AutoCreateScheduleCommand = AutoCreateScheduleCommand.builder()
            .startDt(LocalDate.of(2024, 4, 1))
            .endDt(LocalDate.of(2024, 4, 7))
            .weekdayStartTime(LocalTime.of(10, 0))
            .weekdayEndTime(LocalTime.of(20, 0))
            .weekendStartTime(LocalTime.of(12, 0))
            .weekendEndTime(LocalTime.of(18, 0))
            .lessonTime(50)
            .breakTime(10)
            .build()

        val results = scheduleService.autoCreateSchedule(request)

        log.info { results }

    }
}
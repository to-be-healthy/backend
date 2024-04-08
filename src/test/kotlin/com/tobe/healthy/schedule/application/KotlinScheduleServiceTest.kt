package com.tobe.healthy.schedule.application

import com.tobe.healthy.schedule.domain.dto.`in`.AutoCreateScheduleCommand
import com.tobe.healthy.schedule.repository.ScheduleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime.of

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
            .weekdayStartTime(of(10, 0))
            .weekdayEndTime(of(20, 0))
            .weekendStartTime(of(12, 0))
            .weekendEndTime(of(18, 0))
            .lessonTime(50)
            .breakTime(10)
            .build()

        val results = scheduleService.autoCreateSchedule(request)

        assertThat(results.size).isEqualTo(7)
    }

    @Test
    fun `일정을 등록한다`() {

    }
}
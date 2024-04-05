package com.tobe.healthy.schedule.application

import com.tobe.healthy.schedule.repository.ScheduleRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class KotlinScheduleServiceTest @Autowired constructor(
    private val scheduleRepository: ScheduleRepository
) {

    @Test
    fun registerSchedule() {
        val schedules = scheduleRepository.findAll()
        println("scheduleRepository = ${scheduleRepository}")
    }

}
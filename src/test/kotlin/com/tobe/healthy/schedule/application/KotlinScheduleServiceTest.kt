package com.tobe.healthy.schedule.application

import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class KotlinScheduleServiceTest @Autowired constructor(
    private val scheduleService: ScheduleService,
) : BehaviorSpec(
    {

    },
)

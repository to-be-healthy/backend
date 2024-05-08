package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.schedule.domain.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository

interface TrainerScheduleRepository : JpaRepository<Schedule, Long>, TrainerScheduleRepositoryCustom

package com.tobe.healthy.schedule.repository

import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo
import org.springframework.data.jpa.repository.JpaRepository

interface TrainerScheduleInfoRepository : JpaRepository<TrainerScheduleInfo, Long> {
    fun findOneByTrainerId(trainerId: Long): TrainerScheduleInfo?
}

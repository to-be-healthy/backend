package com.tobe.healthy.schedule.repository

import com.tobe.healthy.schedule.entity.TrainerScheduleClosedDaysInfo
import org.springframework.data.jpa.repository.JpaRepository

interface TrainerScheduleClosedDaysInfoRepository: JpaRepository<TrainerScheduleClosedDaysInfo, Long>{
}

package com.tobe.healthy.schedule.repository

import com.tobe.healthy.schedule.entity.TrainerScheduleInfo

interface TrainerScheduleInfoRepositoryCustom {
    fun findDefaultScheduleByTrainerId(trainerId: Long): TrainerScheduleInfo?
}

package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.TRAINER_SCHEDULE_NOT_FOUND
import com.tobe.healthy.schedule.entity.`in`.TrainerSchedule
import com.tobe.healthy.schedule.entity.`in`.TrainerScheduleByDate
import com.tobe.healthy.schedule.entity.out.TrainerDefaultLessonTimeResult
import com.tobe.healthy.schedule.entity.out.TrainerScheduleByDateResult
import com.tobe.healthy.schedule.entity.out.TrainerScheduleResult
import com.tobe.healthy.schedule.repository.TrainerScheduleInfoRepository
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TrainerScheduleService(
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val trainerScheduleInfoRepository: TrainerScheduleInfoRepository
) {
    fun findDefaultLessonTime(trainerId: Long): TrainerDefaultLessonTimeResult {
        val trainerScheduleInfo = trainerScheduleInfoRepository.findByTrainerId(trainerId)
            ?: throw CustomException(TRAINER_SCHEDULE_NOT_FOUND)
        return TrainerDefaultLessonTimeResult.from(trainerScheduleInfo)
    }

    fun findAllSchedule(trainerSchedule: TrainerSchedule, trainerId: Long): TrainerScheduleResult? {
        return trainerScheduleRepository.findAllSchedule(trainerSchedule, trainerId)
    }

    fun findOneTrainerTodaySchedule(queryTrainerSchedule: TrainerScheduleByDate, trainerId: Long): TrainerScheduleByDateResult? {
        return trainerScheduleRepository.findOneTrainerTodaySchedule(queryTrainerSchedule, trainerId)
    }
}

const val ONE_DAY = 1L
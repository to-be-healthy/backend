package com.tobe.healthy.schedule.application

import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonDt
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonInfo
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerDefaultLessonTimeResult
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonInfoResult
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
    fun findDefaultLessonTime(trainerId: Long): RetrieveTrainerDefaultLessonTimeResult? {
        val trainerScheduleInfo = trainerScheduleInfoRepository.findByTrainerId(trainerId)
            ?: return null
        return RetrieveTrainerDefaultLessonTimeResult.from(trainerScheduleInfo)
    }

    fun findAllSchedule(retrieveTrainerScheduleByLessonInfo: RetrieveTrainerScheduleByLessonInfo, trainerId: Long): RetrieveTrainerScheduleByLessonInfoResult? {
        return trainerScheduleRepository.findAllSchedule(retrieveTrainerScheduleByLessonInfo, trainerId)
    }

    fun findOneTrainerTodaySchedule(queryTrainerSchedule: RetrieveTrainerScheduleByLessonDt, trainerId: Long): RetrieveTrainerScheduleByLessonDtResult? {
        return trainerScheduleRepository.findOneTrainerTodaySchedule(queryTrainerSchedule, trainerId)
    }
}

const val ONE_DAY = 1L
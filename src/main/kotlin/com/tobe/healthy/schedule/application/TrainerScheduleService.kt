package com.tobe.healthy.schedule.application

import com.tobe.healthy.common.KotlinCustomPaging
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonDt
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonInfo
import com.tobe.healthy.schedule.domain.dto.out.RetrieveApplicantSchedule
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerDefaultLessonTimeResult
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonInfoResult
import com.tobe.healthy.schedule.repository.TrainerScheduleInfoRepository
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TrainerScheduleService(
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val trainerScheduleInfoRepository: TrainerScheduleInfoRepository
) {
    fun findOneDefaultLessonTime(
        trainerId: Long
    ): RetrieveTrainerDefaultLessonTimeResult? {
        val trainerScheduleInfo = trainerScheduleInfoRepository.findOneByTrainerId(trainerId)
        return RetrieveTrainerDefaultLessonTimeResult.from(trainerScheduleInfo)
    }

    fun findAllSchedule(
        request: RetrieveTrainerScheduleByLessonInfo,
        trainerId: Long
    ): RetrieveTrainerScheduleByLessonInfoResult? {
        val schedules = trainerScheduleRepository.findAllSchedule(request, trainerId)
        return RetrieveTrainerScheduleByLessonInfoResult.from(schedules)
    }

    fun findOneTrainerTodaySchedule(
        request: RetrieveTrainerScheduleByLessonDt,
        trainerId: Long
    ): RetrieveTrainerScheduleByLessonDtResult? {
        return trainerScheduleRepository.findOneTrainerTodaySchedule(request, trainerId)
    }

    fun findAllScheduleByStduentId(
        studentId: Long,
        pageable: Pageable,
        trainerId: Long
    ): KotlinCustomPaging<RetrieveApplicantSchedule> {

        val schedules = trainerScheduleRepository.findAllScheduleByStduentId(studentId, pageable, trainerId)

        val contents = schedules.map { RetrieveApplicantSchedule.from(it) }

        return KotlinCustomPaging(
            content = contents.content,
            pageNumber = contents.pageable.pageNumber,
            pageSize = contents.pageable.pageSize,
            totalPages = contents.totalPages,
            totalElements = contents.totalElements,
            isLast = contents.isLast
        )
    }
}

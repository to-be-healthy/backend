package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.DATETIME_NOT_VALID
import com.tobe.healthy.config.error.ErrorCode.LUNCH_TIME_INVALID
import com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_ALREADY_EXISTS
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_LESS_THAN_30_DAYS
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_NOT_FOUND
import com.tobe.healthy.config.error.ErrorCode.START_DATE_AFTER_END_DATE
import com.tobe.healthy.log
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleRequest
import com.tobe.healthy.schedule.domain.dto.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.AVAILABLE
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration.between
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit.DAYS

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
class TrainerScheduleService(
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository,
) {
    fun registerSchedule(request: RegisterScheduleRequest, trainerId: Long): Boolean {
        validateScheduleDate(request)

        val trainer = memberRepository.findByIdOrNull(trainerId) ?: throw CustomException(MEMBER_NOT_FOUND)

        var lessonDt = request.startDt

        while (isLessonDtBeforeOrEqualsEndDt(request, lessonDt)) {
            var startTime = request.startTime
            while (startTimeIsBefore(request, startTime)) {
                val endTime = startTime.plusMinutes(request.sessionTime.description.toLong())

                if (endTime.isAfter(request.endTime)) {
                    break
                }

                if (request.closedDt.contains(lessonDt)) {
                    lessonDt = lessonDt.plusDays(1)
                    continue
                }

                if (isStartTimeEqualsLunchStartTime(request, startTime)) {
                    val duration = between(request.lunchStartTime, request.lunchEndTime)
                    startTime = startTime.plusMinutes(duration.toMinutes())
                    continue
                }

                val isDuplicateSchedule = trainerScheduleRepository.validateRegisterSchedule(lessonDt, startTime, endTime, trainerId)
                log.info { "isDuplicateSchedule -> ${isDuplicateSchedule}" }
                if (isDuplicateSchedule > 0) {
                    throw CustomException(SCHEDULE_ALREADY_EXISTS)
                }

                val schedule = Schedule.registerSchedule(lessonDt, trainer, startTime, endTime, AVAILABLE)
                trainerScheduleRepository.save(schedule)

                startTime = endTime
            }
            lessonDt = lessonDt.plusDays(1)
        }
        return true
    }

    private fun isStartTimeEqualsLunchStartTime(request: RegisterScheduleRequest, startTime: LocalTime): Boolean {
        return startTime == request?.lunchStartTime
    }

    private fun startTimeIsBefore(request: RegisterScheduleRequest, startTime: LocalTime): Boolean {
        return startTime.isBefore(request.endTime)
    }

    private fun isLessonDtBeforeOrEqualsEndDt(request: RegisterScheduleRequest, lessonDt: LocalDate): Boolean {
        return !lessonDt.isAfter(request.endDt)
    }

    private fun validateScheduleDate(request: RegisterScheduleRequest) {
        if (request.startDt.isAfter(request.endDt)) {
            throw CustomException(START_DATE_AFTER_END_DATE)
        }
        if (request.startTime.isAfter(request.endTime)) {
            throw CustomException(DATETIME_NOT_VALID)
        }
        if (request.lunchStartTime?.isAfter(request.lunchEndTime) == true) {
            throw CustomException(LUNCH_TIME_INVALID)
        }
        if (DAYS.between(request.startDt, request.endDt) > 30) {
            throw CustomException(SCHEDULE_LESS_THAN_30_DAYS)
        }
    }

    fun findAllSchedule(searchCond: ScheduleSearchCond, trainer: Member): List<ScheduleCommandResult?> {
        return trainerScheduleRepository.findAllSchedule(searchCond, trainer)
    }

    fun updateReservationStatusToNoShow(scheduleId: Long, trainerId: Long): ScheduleIdInfo {
        val schedule = trainerScheduleRepository.findScheduleByTrainerId(scheduleId, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        schedule.updateReservationStatusToNoShow()
        return ScheduleIdInfo.from(schedule)
    }

    fun revertReservationStatusToNoShow(scheduleId: Long, trainerId: Long): ScheduleIdInfo {
        val schedule = trainerScheduleRepository.findScheduleByTrainerId(scheduleId, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        schedule.revertReservationStatusToNoShow()
        return ScheduleIdInfo.from(schedule)
    }

    fun registerIndividualSchedule(request: RegisterScheduleCommand, trainerId: Long): Boolean {
        trainerScheduleRepository.findAvailableRegisterSchedule(request, trainerId)?.let {
            throw CustomException(SCHEDULE_ALREADY_EXISTS)
        } ?: run {
            val trainer = memberRepository.findByIdOrNull(trainerId) ?: throw CustomException(MEMBER_NOT_FOUND)
            val entity = Schedule.registerSchedule(request.lessonDt, trainer, request.lessonStartTime, request.lessonEndTime, AVAILABLE)
            trainerScheduleRepository.save(entity)
            return true
        }
    }

    fun cancelTrainerSchedule(scheduleId: Long, trainerId: Long): Boolean {
        val entity = trainerScheduleRepository.findScheduleByTrainerId(scheduleId, trainerId) ?: throw CustomException(SCHEDULE_NOT_FOUND)
        entity.cancelTrainerSchedule()
        return true
    }
}

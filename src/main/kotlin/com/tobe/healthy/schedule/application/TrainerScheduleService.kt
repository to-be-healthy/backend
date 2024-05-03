package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_ALREADY_EXISTS
import com.tobe.healthy.config.error.ErrorCode.SCHEDULE_NOT_FOUND
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleRequest
import com.tobe.healthy.schedule.domain.dto.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
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
    fun registerSchedule(request: RegisterScheduleRequest, trainerId: Long?): Boolean {
        validateScheduleDate(request)

        val trainer = memberRepository.findById(trainerId!!)
            .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }

        var lessonDt: LocalDate = request.startDt

        while (isLessonDtBeforeOrEqualsEndDt(request, lessonDt)) {
            var startTime: LocalTime = request.startTime
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
                    val duration = Duration.between(request.lunchStartTime, request.lunchEndTime)
                    startTime = startTime.plusMinutes(duration.toMinutes())
                    continue
                }

                val isDuplicateSchedule =
                    trainerScheduleRepository.validateRegisterSchedule(lessonDt, startTime, endTime, trainerId)

                if (isDuplicateSchedule == true) {
                    throw CustomException(SCHEDULE_ALREADY_EXISTS)
                }

                val schedule =
                    Schedule.registerSchedule(lessonDt, trainer, startTime, endTime, ReservationStatus.AVAILABLE)
                trainerScheduleRepository.save(schedule)

                startTime = endTime
            }
            lessonDt = lessonDt.plusDays(1)
        }
        return true
    }

    private fun isStartTimeEqualsLunchStartTime(request: RegisterScheduleRequest, startTime: LocalTime): Boolean {
        return startTime == request.lunchStartTime
    }

    private fun startTimeIsBefore(request: RegisterScheduleRequest, startTime: LocalTime): Boolean {
        return startTime.isBefore(request.endTime)
    }

    private fun isLessonDtBeforeOrEqualsEndDt(request: RegisterScheduleRequest, lessonDt: LocalDate): Boolean {
        return !lessonDt.isAfter(request.endDt)
    }

    private fun validateScheduleDate(request: RegisterScheduleRequest) {
        if (request.startDt.isAfter(request.endDt)) {
            throw CustomException(ErrorCode.START_DATE_AFTER_END_DATE)
        }
        if (request.startTime.isAfter(request.endTime)) {
            throw CustomException(ErrorCode.DATETIME_NOT_VALID)
        }
        if (request.lunchStartTime.isAfter(request.lunchEndTime)) {
            throw CustomException(ErrorCode.LUNCH_TIME_INVALID)
        }
        if (DAYS.between(request.startDt, request.endDt) > 30) {
            throw CustomException(ErrorCode.SCHEDULE_LESS_THAN_30_DAYS)
        }
    }

    fun findAllSchedule(searchCond: ScheduleSearchCond, trainer: Member): List<ScheduleCommandResult> {
        return trainerScheduleRepository.findAllSchedule(searchCond, trainer.id, trainer)
    }

    fun updateReservationStatusToNoShow(scheduleId: Long?, memberId: Long?): ScheduleIdInfo {
        val schedule = trainerScheduleRepository.findScheduleByTrainerId(memberId, scheduleId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        schedule?.updateReservationStatusToNoShow()
        return ScheduleIdInfo.from(schedule)
    }

    fun registerIndividualSchedule(request: RegisterScheduleCommand, trainerId: Long?): Boolean {
        trainerScheduleRepository.findAvailableRegisterSchedule(request, trainerId)?.ifPresentOrElse(
            { schedule: Schedule? ->
                val trainer = memberRepository.findById(
                    trainerId!!,
                )
                    .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }
                val entity =
                    Schedule.registerSchedule(
                        request.lessonDt,
                        trainer,
                        request.lessonStartTime,
                        request.lessonEndTime,
                        ReservationStatus.AVAILABLE,
                    )
                trainerScheduleRepository.save(entity)
            },
            {
                throw CustomException(SCHEDULE_ALREADY_EXISTS)
            },
        )
        return true
    }

    fun cancelTrainerSchedule(scheduleId: Long?, memberId: Long?): Boolean {
        val entity = trainerScheduleRepository.findScheduleByTrainerId(memberId, scheduleId) ?: throw CustomException(SCHEDULE_NOT_FOUND)

        entity.cancelTrainerSchedule()

        return true
    }
}

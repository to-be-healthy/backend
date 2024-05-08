package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.*
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.entity.TrainerScheduleClosedDaysInfo
import com.tobe.healthy.schedule.entity.TrainerScheduleInfo
import com.tobe.healthy.schedule.entity.`in`.RegisterDefaultLessonTimeRequest
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleRequest
import com.tobe.healthy.schedule.entity.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.entity.out.RegisterDefaultLessonTimeResponse
import com.tobe.healthy.schedule.repository.TrainerScheduleInfoRepository
import com.tobe.healthy.schedule.repository.schedule_waiting.ScheduleWaitingRepository
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration.between
import java.time.LocalTime
import java.time.temporal.ChronoUnit.DAYS

@Service
@Transactional
class TrainerScheduleService(
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val scheduleWaitingRepository: ScheduleWaitingRepository,
    private val trainerScheduleInfoRepository: TrainerScheduleInfoRepository,
) {
    fun registerSchedule(request: RegisterScheduleRequest, trainerId: Long): Boolean {
        validateScheduleDate(request)

        val trainer = memberRepository.findByIdOrNull(trainerId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val findTrainerSchedule = trainerScheduleInfoRepository.findByTrainerId(trainerId)
            ?: throw CustomException(TRAINER_SCHEDULE_NOT_FOUND)

        var lessonDt = request.startDt
        var startTime = findTrainerSchedule.lessonStartTime
        val schedules = mutableListOf<Schedule>()

        while (!lessonDt.isAfter(request.endDt)) {
            val endTime = startTime.plusMinutes(findTrainerSchedule.lessonTime.description.toLong())

            if (endTime.isAfter(findTrainerSchedule.lessonEndTime)) {
                startTime = findTrainerSchedule.lessonStartTime
                lessonDt = lessonDt.plusDays(ONE_DAY)
                continue
            }

            findTrainerSchedule.trainerScheduleClosedDays?.forEach {
                // 휴무일일 경우
                if (it.closedDays == lessonDt.dayOfWeek) {
                    lessonDt = lessonDt.plusDays(ONE_DAY)
                    return@forEach
                }
            }

            // 점심시간일 경우
            if (isStartTimeEqualsLunchStartTime(findTrainerSchedule.lunchStartTime, startTime)) {
                val duration = between(findTrainerSchedule.lunchStartTime, findTrainerSchedule.lunchEndTime)
                startTime = startTime.plusMinutes(duration.toMinutes())
                continue
            }

            val isDuplicateSchedule =
                trainerScheduleRepository.validateRegisterSchedule(lessonDt, startTime, endTime, trainerId)

            if (isDuplicateSchedule > 0) {
                throw CustomException(SCHEDULE_ALREADY_EXISTS)
            }

            val schedule = Schedule.registerSchedule(lessonDt, trainer, startTime, endTime, AVAILABLE)
            schedules.add(schedule)

            startTime = endTime
        }
        trainerScheduleRepository.saveAll(schedules)
        return true
    }

    private fun isStartTimeEqualsLunchStartTime(lunchStartTime: LocalTime?, startTime: LocalTime): Boolean {
        return startTime == lunchStartTime
    }

    private fun validateScheduleDate(request: RegisterScheduleRequest) {
        if (request.startDt.isAfter(request.endDt)) {
            throw CustomException(START_DATE_AFTER_END_DATE)
        }
        if (DAYS.between(request.startDt, request.endDt) > ONE_MONTH) {
            throw CustomException(SCHEDULE_LESS_THAN_30_DAYS)
        }
    }

    fun findAllSchedule(searchCond: ScheduleSearchCond, trainerId: Long): List<ScheduleCommandResult?> {
        return trainerScheduleRepository.findAllSchedule(searchCond, trainerId)
    }

    fun updateReservationStatusToNoShow(scheduleId: Long, trainerId: Long): ScheduleIdInfo {
        val schedule = trainerScheduleRepository.findScheduleByTrainerId(scheduleId, COMPLETED, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        schedule.updateReservationStatusToNoShow()
        return ScheduleIdInfo.from(schedule)
    }

    fun revertReservationStatusToNoShow(scheduleId: Long, trainerId: Long): ScheduleIdInfo {
        val schedule = trainerScheduleRepository.findScheduleByTrainerId(scheduleId, NO_SHOW, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        schedule.revertReservationStatusToNoShow()
        return ScheduleIdInfo.from(schedule)
    }

    fun registerIndividualSchedule(request: RegisterScheduleCommand, trainerId: Long): Boolean {
        trainerScheduleRepository.findAvailableRegisterSchedule(request, trainerId)?.let {
            throw CustomException(SCHEDULE_ALREADY_EXISTS)
        } ?: let {
            val trainer = memberRepository.findByIdOrNull(trainerId)
                ?: throw CustomException(MEMBER_NOT_FOUND)

            val entity = Schedule.registerSchedule(
                request.lessonDt,
                trainer,
                request.lessonStartTime,
                request.lessonEndTime,
                AVAILABLE,
            )
            trainerScheduleRepository.save(entity)
            return true
        }
    }

    fun cancelTrainerSchedule(scheduleId: Long, trainerId: Long): LocalTime {
        // todo: 2024-05-05 일요일 오후 14:16 등록된 학생이 있는경우 푸시알림등으로 취소되었다는 알림이 필요 - seonwoo_jung
        val entity = trainerScheduleRepository.findScheduleByTrainerId(scheduleId, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        entity.cancelTrainerSchedule()
        return entity.lessonStartTime
    }

    fun updateLessonDtToClosedDay(lessonDt: String, trainerId: Long): Boolean {
        val findSchedule = trainerScheduleRepository.findAllByLessonDtAndTrainerId(lessonDt, trainerId)

        if (findSchedule.isEmpty()) {
            throw CustomException(SCHEDULE_NOT_FOUND)
        }

        findSchedule.forEach {
            it?.updateLessonDtToClosedDay()
            it?.scheduleWaiting?.forEach { waiting -> scheduleWaitingRepository.delete(waiting) }
        }
        return true
    }

    fun registerDefaultLessonTime(
        request: RegisterDefaultLessonTimeRequest,
        trainerId: Long,
    ): RegisterDefaultLessonTimeResponse {
        val findTrainer = memberRepository.findByIdOrNull(trainerId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        trainerScheduleInfoRepository.findByTrainerId(trainerId)?.let {
            it.changeDefaultLessonTime(request)
            it.trainerScheduleClosedDays?.clear()
            it.trainerScheduleClosedDays?.addAll(
                request.closedDt?.map { dayOfWeek ->
                    TrainerScheduleClosedDaysInfo.registerClosedDay(
                        dayOfWeek,
                        it,
                    )
                }?.toMutableList() ?: mutableListOf(),
            )
        } ?: let {
            val trainerScheduleInfo = TrainerScheduleInfo.registerDefaultLessonTime(request, findTrainer)
            val trainerScheduleClosedDaysInfos = mutableListOf<TrainerScheduleClosedDaysInfo>()

            request.closedDt?.forEach { dayOfWeek ->
                trainerScheduleClosedDaysInfos.add(
                    TrainerScheduleClosedDaysInfo.registerClosedDay(
                        dayOfWeek,
                        trainerScheduleInfo,
                    ),
                )
            }

            trainerScheduleInfo.registerTrainerScheduleClosedDays(trainerScheduleClosedDaysInfos)
            trainerScheduleInfoRepository.save(trainerScheduleInfo)
        }

        return RegisterDefaultLessonTimeResponse.from(request)
    }
}

const val ONE_MONTH = 30
const val ONE_DAY = 1L

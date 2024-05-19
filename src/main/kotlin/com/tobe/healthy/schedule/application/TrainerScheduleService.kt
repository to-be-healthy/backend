package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.*
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.entity.TrainerScheduleClosedDaysInfo
import com.tobe.healthy.schedule.entity.TrainerScheduleInfo
import com.tobe.healthy.schedule.entity.`in`.*
import com.tobe.healthy.schedule.entity.out.*
import com.tobe.healthy.schedule.repository.TrainerScheduleInfoRepository
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration.between
import java.time.LocalTime

@Service
@Transactional
class TrainerScheduleService(
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val trainerScheduleInfoRepository: TrainerScheduleInfoRepository
) {
    fun registerSchedule(request: RegisterScheduleRequest, trainerId: Long): ScheduleRegisterResponse {
        val trainer = memberRepository.findByIdOrNull(trainerId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val findTrainerSchedule = trainerScheduleInfoRepository.findByTrainerId(trainerId)
            ?: throw CustomException(TRAINER_SCHEDULE_NOT_FOUND)

        var lessonDt = request.lessonStartDt
        var startTime = findTrainerSchedule.lessonStartTime
        val schedules = mutableListOf<Schedule>()

        while (!lessonDt.isAfter(request.lessonEndDt)) {
            var endTime = startTime.plusMinutes(findTrainerSchedule.lessonTime.description.toLong())

            if (endTime.isAfter(findTrainerSchedule.lessonEndTime)) {
                startTime = findTrainerSchedule.lessonStartTime
                lessonDt = lessonDt.plusDays(ONE_DAY)
                continue
            }

            val isClosedDay = findTrainerSchedule.trainerScheduleClosedDays?.any {
                it.closedDays == lessonDt.dayOfWeek
            } ?: false

            if (isClosedDay) {
                // 휴무일일 경우
                while (!startTime.isAfter(endTime)) {
                    val schedule = Schedule.registerSchedule(lessonDt, trainer, startTime, endTime, DISABLED)
                    schedules.add(schedule)
                    startTime = endTime
                    endTime = startTime.plusMinutes(findTrainerSchedule.lessonTime.description.toLong())
                }
                startTime = findTrainerSchedule.lessonStartTime
                lessonDt = lessonDt.plusDays(ONE_DAY)
                continue
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

        return ScheduleRegisterResponse.from(schedules, findTrainerSchedule)
    }

    private fun isStartTimeEqualsLunchStartTime(lunchStartTime: LocalTime?, startTime: LocalTime): Boolean {
        return startTime == lunchStartTime
    }

    fun findAllSchedule(searchCond: ScheduleSearchCond, trainerId: Long): LessonResponse? {
        return trainerScheduleRepository.findAllSchedule(searchCond, trainerId)
    }

    fun findOneTrainerTodaySchedule(searchCond: TrainerTodayScheduleSearchCond, trainerId: Long): TrainerTodayScheduleResponse? {
        return trainerScheduleRepository.findOneTrainerTodaySchedule(searchCond, trainerId)
    }

    fun updateReservationStatusToNoShow(reservationStatus: ReservationStatus, scheduleId: Long, trainerId: Long): ScheduleIdInfo {
        val schedule = trainerScheduleRepository.findScheduleByTrainerId(scheduleId, reservationStatus, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        schedule.updateReservationStatusToNoShow(reservationStatus)
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
        }
        return true
    }

    fun registerDefaultLessonTime(request: RegisterDefaultLessonTimeRequest, trainerId: Long): RegisterDefaultLessonTimeResponse {
        val findTrainer = memberRepository.findByIdOrNull(trainerId)
                ?: throw CustomException(MEMBER_NOT_FOUND)

        trainerScheduleInfoRepository.findByTrainerId(trainerId)?.let {
            it.changeDefaultLessonTime(request)
            it.trainerScheduleClosedDays?.clear()
            it.trainerScheduleClosedDays?.addAll(
                request.closedDays?.map { dayOfWeek ->
                    TrainerScheduleClosedDaysInfo.registerClosedDay(
                        dayOfWeek,
                        it,
                    )
                }?.toMutableList() ?: mutableListOf(),
            )
        } ?: let {
            val trainerScheduleInfo = TrainerScheduleInfo.registerDefaultLessonTime(request, findTrainer)
            val trainerScheduleClosedDaysInfos = mutableListOf<TrainerScheduleClosedDaysInfo>()

            request.closedDays?.forEach { dayOfWeek ->
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

    fun findDefaultLessonTime(trainerId: Long): TrainerScheduleResponse {
        val trainerScheduleInfo = trainerScheduleInfoRepository.findByTrainerId(trainerId)
            ?: throw CustomException(TRAINER_SCHEDULE_NOT_FOUND)
        return TrainerScheduleResponse.from(trainerScheduleInfo)
    }

    fun registerScheduleForStudent(scheduleId: Long, studentId: Long, trainerId: Long): RegisterScheduleForStudentResponse {
        val schedule = trainerScheduleRepository.findScheduleByTrainerId(scheduleId, AVAILABLE, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)

        val findStudent = memberRepository.findById(studentId)
            .orElseThrow { throw CustomException(MEMBER_NOT_FOUND) }

        schedule.registerSchedule(findStudent)

        return RegisterScheduleForStudentResponse.from(schedule, findStudent);
    }
}

const val ONE_MONTH = 31
const val ONE_DAY = 1L

package com.tobe.healthy.schedule.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterDefaultLessonTime
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterSchedule
import com.tobe.healthy.schedule.domain.dto.`in`.CommandUpdateScheduleStatus
import com.tobe.healthy.schedule.domain.dto.out.*
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.*
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleClosedDaysInfo
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo
import com.tobe.healthy.schedule.repository.TrainerScheduleInfoRepository
import com.tobe.healthy.schedule.repository.schedulewaiting.ScheduleWaitingRepository
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SUNDAY
import java.time.Duration.between
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Service
@Transactional
class TrainerScheduleCommandService(
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val trainerScheduleInfoRepository: TrainerScheduleInfoRepository,
    private val scheduleWaitingRepository: ScheduleWaitingRepository
) {

    fun registerDefaultLessonTime(
        request: CommandRegisterDefaultLessonTime,
        trainerId: Long
    ): CommandRegisterDefaultLessonTimeResult {
        val findTrainer = findMemberById(trainerId)

        trainerScheduleInfoRepository.findOneByTrainerId(trainerId)
            ?.let {
                val closedDays = createTrainerScheduleClosedDays(request, it)
                it.changeDefaultLessonTime(request, closedDays)
            }
            ?: let {
                val trainerScheduleInfo = TrainerScheduleInfo.registerDefaultLessonTime(request, findTrainer)

                val closedDays = createTrainerScheduleClosedDays(request, trainerScheduleInfo)

                trainerScheduleInfo.trainerScheduleClosedDays.addAll(closedDays)

                trainerScheduleInfoRepository.save(trainerScheduleInfo)
            }

        return CommandRegisterDefaultLessonTimeResult.from(request)
    }

    private fun createTrainerScheduleClosedDays(
        request: CommandRegisterDefaultLessonTime,
        trainerScheduleInfo: TrainerScheduleInfo
    ): MutableList<TrainerScheduleClosedDaysInfo> {
        return request.closedDays?.map {
                closedDay -> TrainerScheduleClosedDaysInfo.registerClosedDay(closedDay, trainerScheduleInfo)
        }?.toMutableList() ?: mutableListOf()
    }

    fun registerSchedule(
        request: CommandRegisterSchedule,
        trainerId: Long
    ): CommandRegisterScheduleResult {
        val trainer = findMemberById(trainerId)

        val trainerScheduleInfo = trainerScheduleInfoRepository.findOneByTrainerId(trainerId)
            ?: throw CustomException(TRAINER_SCHEDULE_NOT_FOUND)

        // 이미 등록된 일정이 있는지 조회
        isScheduleExisting(trainerScheduleInfo, request, trainerId)

        var lessonDt = request.lessonStartDt
        var startTime = trainerScheduleInfo.lessonStartTime
        val schedules = mutableListOf<Schedule>()

        // 일정 등록 시작
        while (!lessonDt.isAfter(request.lessonEndDt)) {
            var endTime = startTime.plusMinutes(trainerScheduleInfo.lessonTime.description.toLong())

            if (endTime.isAfter(trainerScheduleInfo.lessonEndTime)) {
                startTime = trainerScheduleInfo.lessonStartTime
                lessonDt = lessonDt.plusDays(ONE_DAY)
                continue
            }

            // 휴무일일 경우
            if (isClosedDay(trainerScheduleInfo, lessonDt)) {
                while (!startTime.isAfter(endTime)) {
                    val schedule = Schedule.registerSchedule(lessonDt, trainer, startTime, endTime, DISABLED)
                    schedules.add(schedule)
                    startTime = endTime
                    endTime = startTime.plusMinutes(trainerScheduleInfo.lessonTime.description.toLong())
                }
                startTime = trainerScheduleInfo.lessonStartTime
                lessonDt = lessonDt.plusDays(ONE_DAY)
                continue
            }

            // 점심시간일 경우
            if (isStartTimeEqualsLunchStartTime(trainerScheduleInfo.lunchStartTime, startTime)) {
                val duration = between(trainerScheduleInfo.lunchStartTime, trainerScheduleInfo.lunchEndTime)
                val schedule = Schedule.registerSchedule(lessonDt, trainer, trainerScheduleInfo.lunchStartTime, trainerScheduleInfo.lunchEndTime, DISABLED)
                schedules.add(schedule)
                startTime = startTime.plusMinutes(duration.toMinutes())
                continue
            }

            val schedule = Schedule.registerSchedule(lessonDt, trainer, startTime, endTime, AVAILABLE)
            schedules.add(schedule)

            startTime = endTime
        }

        trainerScheduleRepository.saveAll(schedules)

        return CommandRegisterScheduleResult.from(schedules, trainerScheduleInfo)
    }

    private fun isClosedDay(
        trainerScheduleInfo: TrainerScheduleInfo,
        lessonDt: LocalDate
    ): Boolean {
        return trainerScheduleInfo.trainerScheduleClosedDays.any {
            it.closedDays == lessonDt.dayOfWeek
        }
    }

    fun updateScheduleStatus(
        request: CommandUpdateScheduleStatus,
        status: ReservationStatus,
        memberId: Long
    ): List<CommandScheduleStatusResult> {

        val schedules: List<Schedule>

        when (status) {

            AVAILABLE -> {
                schedules = trainerScheduleRepository.findAllSchedule(request.scheduleIds, DISABLED, memberId)

                if (schedules.isNullOrEmpty()) {
                    throw CustomException(SCHEDULE_NOT_FOUND)
                }
                schedules.forEach {
                    it.updateLessonDtToAvailableDay()
                }
            }

            DISABLED -> {
                schedules = trainerScheduleRepository.findAllSchedule(request.scheduleIds, listOf(AVAILABLE, COMPLETED), memberId)

                if (schedules.isNullOrEmpty()) {
                    throw CustomException(SCHEDULE_NOT_FOUND)
                }

                schedules.forEach {
                    it.updateScheduleToDisabled()
                    if (!it.scheduleWaiting.isNullOrEmpty()) {
                        scheduleWaitingRepository.deleteAll(it.scheduleWaiting!!)
                    }
                }
            }

            else -> {
                throw CustomException(RESERVATION_STATUS_NOT_FOUND)
            }
        }
        return schedules.map { CommandScheduleStatusResult.from(it) }
    }

    fun registerStudentInTrainerSchedule(
        scheduleId: Long,
        studentId: Long,
        trainerId: Long
    ): CommandRegisterScheduleByStudentResult {

        val schedule = trainerScheduleRepository.findAllSchedule(scheduleId, AVAILABLE, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)

        val findStudent = memberRepository.findById(studentId)
            .orElseThrow { throw CustomException(MEMBER_NOT_FOUND) }

        schedule.registerSchedule(findStudent)

        return CommandRegisterScheduleByStudentResult.from(schedule, findStudent)
    }

    fun cancelStudentReservation(
        scheduleId: Long,
        trainerId: Long
    ): CommandCancelStudentReservationResult {

        // todo: 2024-05-05 일요일 오후 14:16 등록된 학생이 있는경우 푸시알림등으로 취소되었다는 알림이 필요 - seonwoo_jung
        val entity = trainerScheduleRepository.findAllSchedule(scheduleId, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)

        val applicant = entity.applicant

        if (!entity.scheduleWaiting.isNullOrEmpty()) {
            entity.cancelMemberSchedule(entity.scheduleWaiting!![0])
            scheduleWaitingRepository.delete(entity.scheduleWaiting!![0])
        } else {
            entity.cancelMemberSchedule()
        }

        return CommandCancelStudentReservationResult.from(entity, applicant)
    }

    fun updateReservationStatusToNoShow(
        scheduleId: Long,
        trainerId: Long
    ): ScheduleIdInfo {
        val schedule = trainerScheduleRepository.findAllSchedule(scheduleId, COMPLETED, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        schedule.updateReservationStatusToNoShow(NO_SHOW)
        return ScheduleIdInfo.from(schedule)
    }

    fun cancelReservationStatusToNoShow(
        scheduleId: Long,
        trainerId: Long
    ): ScheduleIdInfo {
        val schedule = trainerScheduleRepository.findAllSchedule(scheduleId, NO_SHOW, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)
        schedule.updateReservationStatusToNoShow(COMPLETED)
        return ScheduleIdInfo.from(schedule)
    }

    private fun isScheduleExisting(
        trainerScheduleInfo: TrainerScheduleInfo,
        request: CommandRegisterSchedule,
        trainerId: Long
    ) {
        val isDuplicateSchedule = trainerScheduleRepository.validateDuplicateSchedule(trainerScheduleInfo, request, trainerId)

        if (isDuplicateSchedule) {
            throw CustomException(SCHEDULE_ALREADY_EXISTS)
        }
    }

    private fun findMemberById(trainerId: Long) = (memberRepository.findByIdOrNull(trainerId)
        ?: throw CustomException(MEMBER_NOT_FOUND))

    fun deleteDisabledSchedule() {
        val today = LocalDate.now()
        val startOfLastWeek = today.with(TemporalAdjusters.previous(MONDAY))
        val endOfLastWeek = startOfLastWeek.with(TemporalAdjusters.nextOrSame(SUNDAY))
        val disabledSchedule = trainerScheduleRepository.findAllDisabledSchedule(startOfLastWeek, endOfLastWeek)
        trainerScheduleRepository.deleteAll(disabledSchedule)
    }

    private fun isStartTimeEqualsLunchStartTime(lunchStartTime: LocalTime?, startTime: LocalTime): Boolean {
        return startTime == lunchStartTime
    }
}
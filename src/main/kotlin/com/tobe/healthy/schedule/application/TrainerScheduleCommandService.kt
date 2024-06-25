package com.tobe.healthy.schedule.application

import com.tobe.healthy.common.LessonTimeFormatter.lessonStartDateTimeFormatter
import com.tobe.healthy.common.error.CustomException
import com.tobe.healthy.common.error.ErrorCode.*
import com.tobe.healthy.common.event.CustomEventPublisher
import com.tobe.healthy.common.event.EventType.NOTIFICATION
import com.tobe.healthy.common.event.EventType.SCHEDULE_CANCEL
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.notification.domain.dto.`in`.CommandSendNotification
import com.tobe.healthy.notification.domain.entity.NotificationCategory.SCHEDULE
import com.tobe.healthy.notification.domain.entity.NotificationType.CANCEL
import com.tobe.healthy.notification.domain.entity.NotificationType.RESERVE
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
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository
import com.tobe.healthy.schedule.repository.waiting.ScheduleWaitingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SUNDAY
import java.time.Duration.between
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Service
@Transactional
class TrainerScheduleCommandService(
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository,
    private val trainerScheduleInfoRepository: TrainerScheduleInfoRepository,
    private val scheduleWaitingRepository: ScheduleWaitingRepository,
    private val notificationPublisher: CustomEventPublisher<CommandSendNotification>,
    private val eventPublisher: CustomEventPublisher<Long>
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

        if (request.closedDays?.size == 7) {
            throw IllegalArgumentException("모든 요일이 휴무일이 될 수 없습니다.")
        }

        return request.closedDays?.map { closedDay ->
            TrainerScheduleClosedDaysInfo.registerClosedDay(closedDay, trainerScheduleInfo)
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

        // 등록 시작 일자
        var lessonDt = request.lessonStartDt
        val lessonEndDt = request.lessonEndDt
        val lessonTime = trainerScheduleInfo.lessonTime.description.toLong()

        val schedules = mutableListOf<Schedule>()

        // 일정 등록 시작
        while (!lessonDt.isAfter(lessonEndDt)) {

            var defaultLessonStartTime = LocalDateTime.of(lessonDt, trainerScheduleInfo.lessonStartTime)
            var defaultLessonEndTime = LocalDateTime.of(lessonDt, trainerScheduleInfo.lessonEndTime)
            var dayLessonStartTime = LocalDateTime.of(lessonDt, LocalTime.of(6, 0))
            var dayLessonEndTime = LocalDateTime.of(lessonDt.plusDays(1), LocalTime.of(0, 0))

            // 휴무일일 경우
            if (isClosedDay(trainerScheduleInfo, lessonDt)) {
                generateDisabledSchedules(schedules, lessonDt, trainer, dayLessonStartTime, dayLessonEndTime, lessonTime)
                lessonDt = lessonDt.plusDays(ONE_DAY)
                continue
            }

            generateDisabledSchedules(schedules, lessonDt, trainer, dayLessonStartTime, defaultLessonStartTime, lessonTime)
            generateAvailableSchedules(schedules, lessonDt, trainer, defaultLessonStartTime, defaultLessonEndTime, trainerScheduleInfo, lessonTime)
            generateDisabledSchedules(schedules, lessonDt, trainer, defaultLessonEndTime, dayLessonEndTime, lessonTime)

            lessonDt = lessonDt.plusDays(ONE_DAY)
        }

        trainerScheduleRepository.saveAll(schedules)

        return CommandRegisterScheduleResult.from(schedules, trainerScheduleInfo)
    }

    private fun isStartTimeEqualsLunchStartTime(lunchStartTime: LocalTime?, startTime: LocalTime): Boolean {
        return startTime == lunchStartTime
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
                schedules = trainerScheduleRepository.findAllSchedule(request.scheduleIds!!, DISABLED, memberId)

                if (schedules.isEmpty()) {
                    throw CustomException(SCHEDULE_NOT_FOUND)
                }
                schedules.forEach {
                    it.updateLessonDtToAvailableDay()
                }
            }

            DISABLED -> {
                schedules = trainerScheduleRepository.findAllSchedule(
                    request.scheduleIds!!,
                    listOf(AVAILABLE, COMPLETED),
                    memberId
                )

                if (schedules.isEmpty()) {
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

        // 트레이너가 일정 등록시 학생에게 알림
        val notification = CommandSendNotification(
            title = RESERVE.description,
            content = RESERVE.content.format(
                schedule.trainer.name,
                schedule.applicant!!.name,
                LocalDateTime.of(schedule.lessonDt, schedule.lessonStartTime).format(lessonStartDateTimeFormatter())
            ),
            receiverIds = listOf(schedule.applicant!!.id),
            notificationType = RESERVE,
            notificationCategory = SCHEDULE
        )

        notificationPublisher.publish(notification, NOTIFICATION)

        return CommandRegisterScheduleByStudentResult.from(schedule, findStudent)
    }

    fun cancelStudentReservation(
        scheduleId: Long,
        trainerId: Long
    ): CommandCancelStudentReservationResult {

        val schedule = trainerScheduleRepository.findAllSchedule(scheduleId, trainerId)
            ?: throw CustomException(SCHEDULE_NOT_FOUND)

        val applicantId = schedule.applicant?.id
        val applicantName = schedule.applicant?.name

        schedule.cancelMemberSchedule()

        // 트레이너가 일정 등록시 학생에게 알림
        val notification = CommandSendNotification(
            title = CANCEL.description,
            content = CANCEL.content.format(
                schedule.trainer.name,
                applicantName,
                LocalDateTime.of(schedule.lessonDt, schedule.lessonStartTime).format(lessonStartDateTimeFormatter())
            ),
            receiverIds = listOf(applicantId!!),
            notificationType = CANCEL,
            notificationCategory = SCHEDULE
        )

        notificationPublisher.publish(notification, NOTIFICATION)

        eventPublisher.publish(schedule.id, SCHEDULE_CANCEL)

        return CommandCancelStudentReservationResult.from(schedule, applicantId, applicantName)
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
        val isDuplicateSchedule =
            trainerScheduleRepository.validateDuplicateSchedule(trainerScheduleInfo, request, trainerId)

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

    private fun generateDisabledSchedules(
        schedules: MutableList<Schedule>,
        lessonDt: LocalDate,
        trainer: Member,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        lessonTime: Long
    ) {
        var currentTime = startTime
        while (currentTime.isBefore(endTime)) {
            val schedule = Schedule.registerSchedule(lessonDt, trainer, currentTime.toLocalTime(), currentTime.plusMinutes(lessonTime).toLocalTime(), DISABLED)
            schedules.add(schedule)
            currentTime = currentTime.plusMinutes(lessonTime)
        }
    }

    private fun generateAvailableSchedules(
        schedules: MutableList<Schedule>,
        lessonDt: LocalDate,
        trainer: Member,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        trainerScheduleInfo: TrainerScheduleInfo,
        lessonTime: Long
    ) {
        var currentTime = startTime
        while (currentTime.isBefore(endTime)) {
            if (isStartTimeEqualsLunchStartTime(trainerScheduleInfo.lunchStartTime, currentTime.toLocalTime())) {
                val duration = between(trainerScheduleInfo.lunchStartTime, trainerScheduleInfo.lunchEndTime)
                schedules.add(Schedule.registerSchedule(lessonDt, trainer, trainerScheduleInfo.lunchStartTime, trainerScheduleInfo.lunchEndTime, DISABLED))
                currentTime = currentTime.plusMinutes(duration.toMinutes())
            } else {
                schedules.add(Schedule.registerSchedule(
                    lessonDt,
                    trainer,
                    currentTime.toLocalTime(),
                    currentTime.plusMinutes(lessonTime).toLocalTime(),
                    AVAILABLE
                ))
                currentTime = currentTime.plusMinutes(lessonTime)
            }
        }
    }

    companion object {
        const val ONE_DAY = 1L
    }
}
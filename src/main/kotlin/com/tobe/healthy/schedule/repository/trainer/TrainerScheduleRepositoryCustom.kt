package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterIndividualSchedule
import com.tobe.healthy.schedule.domain.dto.`in`.TrainerSchedule
import com.tobe.healthy.schedule.domain.dto.`in`.TrainerScheduleByDate
import com.tobe.healthy.schedule.domain.dto.out.TrainerScheduleByDateResult
import com.tobe.healthy.schedule.domain.dto.out.TrainerScheduleResult
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

interface TrainerScheduleRepositoryCustom {
    fun findAllSchedule(trainerSchedule: TrainerSchedule, trainerId: Long): TrainerScheduleResult?
    fun findOneTrainerTodaySchedule(queryTrainerSchedule: TrainerScheduleByDate, trainerId: Long): TrainerScheduleByDateResult?
    fun findAvailableRegisterSchedule(request: CommandRegisterIndividualSchedule, trainerId: Long): Schedule?
    fun validateRegisterSchedule(lessonDt: LocalDate, startTime: LocalTime, endTime: LocalTime, trainerId: Long): Long
    fun findAvailableWaitingId(scheduleId: Long): Optional<Schedule>
    fun findScheduleByTrainerId(scheduleId: Long, reservationStatus: ReservationStatus, trainerId: Long): Schedule?
    fun findScheduleByTrainerId(scheduleId: Long, trainerId: Long): Schedule?
    fun findAllByLessonDtAndTrainerId(lessonDt: String, trainerId: Long): List<Schedule?>
    fun findAllDisabledSchedule(lessonStartDt: LocalDate, lessonEndDt: LocalDate, trainerId: Long): List<Schedule?>
}

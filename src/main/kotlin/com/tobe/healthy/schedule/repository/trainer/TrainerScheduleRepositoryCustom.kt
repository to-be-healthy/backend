package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.entity.`in`.ScheduleSearchCond
import java.time.LocalDate
import java.time.LocalTime
import java.util.Optional

interface TrainerScheduleRepositoryCustom {
    fun findAllSchedule(searchCond: ScheduleSearchCond, member: Member): List<ScheduleCommandResult?>
    fun findAvailableRegisterSchedule(request: RegisterScheduleCommand, trainerId: Long): Schedule?
    fun validateRegisterSchedule(lessonDt: LocalDate, startTime: LocalTime, localTime: LocalTime, trainerId: Long): Long
    fun findAvailableWaitingId(scheduleId: Long): Optional<Schedule>
    fun findScheduleByTrainerId(scheduleId: Long, reservationStatus: ReservationStatus, trainerId: Long): Schedule?
    fun findScheduleByTrainerId(scheduleId: Long, trainerId: Long): Schedule?
    fun findAllByLessonDtAndTrainerId(lessonDt: String, trainerId: Long): List<Schedule?>
}

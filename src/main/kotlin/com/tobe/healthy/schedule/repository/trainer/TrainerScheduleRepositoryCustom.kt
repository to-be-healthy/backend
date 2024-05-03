package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.domain.dto.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalTime

interface TrainerScheduleRepositoryCustom {
    fun findAllSchedule(searchCond: ScheduleSearchCond, member: Member): List<ScheduleCommandResult?>
    fun findAvailableRegisterSchedule(request: RegisterScheduleCommand, trainerId: Long): Schedule?
    fun validateRegisterSchedule(lessonDt: LocalDate, startTime: LocalTime, localTime: LocalTime, trainerId: Long): Long
}

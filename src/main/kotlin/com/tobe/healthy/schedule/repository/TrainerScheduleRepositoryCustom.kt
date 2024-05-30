package com.tobe.healthy.schedule.repository

import com.tobe.healthy.lessonhistory.domain.dto.`in`.UnwrittenLessonHistorySearchCond
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterSchedule
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonDt
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonInfo
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo
import java.time.LocalDate
import java.util.*

interface TrainerScheduleRepositoryCustom {
    fun findOneTrainerTodaySchedule(request: RetrieveTrainerScheduleByLessonDt, trainerId: Long): RetrieveTrainerScheduleByLessonDtResult?
    fun findOneTrainerTodaySchedule(trainerId: Long): RetrieveTrainerScheduleByLessonDtResult?
    fun validateDuplicateSchedule(trainerScheduleInfo: TrainerScheduleInfo, request: CommandRegisterSchedule, trainerId: Long): Boolean
    fun findAvailableWaitingId(scheduleId: Long): Optional<Schedule>
    fun findAllSchedule(request: RetrieveTrainerScheduleByLessonInfo, trainerId: Long): List<Schedule>
    fun findAllSchedule(scheduleIds: List<Long>, reservationStatus: ReservationStatus, trainerId: Long): List<Schedule>
    fun findAllSchedule(scheduleIds: List<Long>, reservationStatus: List<ReservationStatus>, trainerId: Long): List<Schedule>
    fun findAllSchedule(scheduleId: Long, reservationStatus: ReservationStatus, trainerId: Long): Schedule?
    fun findAllSchedule(scheduleId: Long, trainerId: Long): Schedule?
    fun findAllDisabledSchedule(lessonStartDt: LocalDate, lessonEndDt: LocalDate): List<Schedule?>
    fun findAllUnwrittenLessonHistory(request: UnwrittenLessonHistorySearchCond, memberId: Long): List<Schedule>
    fun findAllSimpleLessonHistoryByMemberId(studentId: Long, trainerId: Long): List<Schedule>
}

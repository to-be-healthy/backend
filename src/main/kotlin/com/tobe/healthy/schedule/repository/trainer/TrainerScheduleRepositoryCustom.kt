package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.lessonhistory.domain.dto.`in`.UnwrittenLessonHistorySearchCond
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterIndividualSchedule
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonDt
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonInfo
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonInfoResult
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

interface TrainerScheduleRepositoryCustom {
    fun findAllSchedule(retrieveTrainerScheduleByLessonInfo: RetrieveTrainerScheduleByLessonInfo, trainerId: Long): RetrieveTrainerScheduleByLessonInfoResult?
    fun findOneTrainerTodaySchedule(queryTrainerSchedule: RetrieveTrainerScheduleByLessonDt, trainerId: Long): RetrieveTrainerScheduleByLessonDtResult?
    fun findOneTrainerTodaySchedule(trainerId: Long): RetrieveTrainerScheduleByLessonDtResult?
    fun findAvailableRegisterSchedule(request: CommandRegisterIndividualSchedule, trainerId: Long): Schedule?
    fun validateRegisterSchedule(lessonDt: LocalDate, startTime: LocalTime, endTime: LocalTime, trainerId: Long): Long
    fun findAvailableWaitingId(scheduleId: Long): Optional<Schedule>
    fun findScheduleByTrainerId(scheduleId: Long, reservationStatus: ReservationStatus, trainerId: Long): Schedule?
    fun findScheduleByTrainerId(scheduleId: Long, trainerId: Long): Schedule?
    fun findAllByLessonDtAndTrainerId(lessonDt: String, trainerId: Long): List<Schedule?>
    fun findAllDisabledSchedule(lessonStartDt: LocalDate, lessonEndDt: LocalDate): List<Schedule?>
    fun findAllUnwrittenLessonHistory(request: UnwrittenLessonHistorySearchCond, memberId: Long): List<Schedule>
    fun findAllSimpleLessonHistoryByMemberId(studentId: Long, trainerId: Long): List<Schedule>
}

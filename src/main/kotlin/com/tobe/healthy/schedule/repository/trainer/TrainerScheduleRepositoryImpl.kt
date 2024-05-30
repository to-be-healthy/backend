package com.tobe.healthy.schedule.repository.trainer

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions.stringTemplate
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.lessonhistory.domain.dto.`in`.UnwrittenLessonHistorySearchCond
import com.tobe.healthy.lessonhistory.domain.entity.QLessonHistory.lessonHistory
import com.tobe.healthy.member.domain.entity.QMember
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterSchedule
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonDt
import com.tobe.healthy.schedule.domain.dto.`in`.RetrieveTrainerScheduleByLessonInfo
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonInfoResult
import com.tobe.healthy.schedule.domain.entity.QSchedule.schedule
import com.tobe.healthy.schedule.domain.entity.QScheduleWaiting.scheduleWaiting
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.DISABLED
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Repository
class TrainerScheduleRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TrainerScheduleRepositoryCustom {

    override fun findAllSchedule(
        request: RetrieveTrainerScheduleByLessonInfo,
        trainerId: Long
    ): List<Schedule> {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(schedule.applicant, QMember("applicant")).fetchJoin()
            .leftJoin(schedule.scheduleWaiting, scheduleWaiting).fetchJoin()
            .where(
                lessonDtMonthEq(request.lessonDt),
                lessonDtBetween(
                    request.lessonStartDt,
                    request.lessonEndDt
                ),
                trainerIdEq(trainerId)
            )
            .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
            .fetch()
    }

    override fun findOneTrainerTodaySchedule(
        request: RetrieveTrainerScheduleByLessonDt,
        trainerId: Long
    ): RetrieveTrainerScheduleByLessonDtResult? {
        val results = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(schedule.applicant, QMember("applicant")).fetchJoin()
            .where(
                lessonDtEq(request.lessonDt),
                trainerIdEq(trainerId),
                reservationStatusEq(COMPLETED)
            )
            .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
            .fetch()

        val scheduleCount = queryFactory
            .select(schedule.count())
            .from(schedule)
            .where(
                lessonDtEq(request.lessonDt),
                trainerIdEq(trainerId),
                reservationStatusEq(COMPLETED)
            )
            .fetchOne()

        val response = RetrieveTrainerScheduleByLessonInfoResult.from(results)

        response.let {
            val trainerTodaySchedule = RetrieveTrainerScheduleByLessonDtResult(
                trainerName = response.trainerName,
                scheduleTotalCount = scheduleCount!!,
            )

            response.schedule.forEach { (key) ->
                response.schedule[key]?.filter {
                    if (it?.lessonStartTime?.isBefore(LocalTime.now()) == true) {
                        trainerTodaySchedule.before.add(it)
                    } else {
                        trainerTodaySchedule.after.add(it)
                    }
                }
            }
            return trainerTodaySchedule

        }
    }

    override fun findOneTrainerTodaySchedule(trainerId: Long): RetrieveTrainerScheduleByLessonDtResult? {
        val results = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(schedule.applicant, QMember("applicant")).fetchJoin()
            .where(
                schedule.lessonDt.eq(LocalDate.now()),
                trainerIdEq(trainerId),
                reservationStatusEq(COMPLETED)
            )
            .orderBy(
                schedule.lessonDt.asc(),
                schedule.lessonStartTime.asc()
            )
            .fetch()

        val scheduleCount = queryFactory
            .select(schedule.count())
            .from(schedule)
            .where(
                schedule.lessonDt.eq(LocalDate.now()),
                trainerIdEq(trainerId),
                reservationStatusEq(COMPLETED)
            )
            .fetchOne()


        val response = RetrieveTrainerScheduleByLessonInfoResult.from(results)

        response.let {
            val trainerTodaySchedule = RetrieveTrainerScheduleByLessonDtResult(
                trainerName = response.trainerName,
                scheduleTotalCount = scheduleCount!!,
            )

            response.schedule.forEach { (key) ->
                response.schedule[key]?.filter {
                    if (it?.lessonStartTime?.isBefore(LocalTime.now()) == true) {
                        trainerTodaySchedule.before.add(it)
                    } else {
                        trainerTodaySchedule.after.add(it)
                    }
                }
            }
            return trainerTodaySchedule
        }
    }

    override fun validateDuplicateSchedule(
        trainerScheduleInfo: TrainerScheduleInfo,
        request: CommandRegisterSchedule,
        trainerId: Long
    ): Boolean {
        val count = queryFactory
            .select(schedule.count())
            .from(schedule)
            .where(
                lessonDtBetween(request.lessonStartDt, request.lessonEndDt),
                trainerIdEq(trainerId),
                schedule.lessonStartTime.between(trainerScheduleInfo.lessonStartTime, trainerScheduleInfo.lessonEndTime),
                schedule.lessonEndTime.between(trainerScheduleInfo.lessonStartTime, trainerScheduleInfo.lessonEndTime)
            )
            .fetchOne() ?: 0L
        return count > 0
    }

    override fun findAvailableWaitingId(scheduleId: Long): Optional<Schedule> {
        val result = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.scheduleWaiting, scheduleWaiting)
            .where(
                scheduleIdEq(scheduleId),
                reservationStatusEq(COMPLETED),
                applicantIsNotNull()
            )
            .fetchOne()
        return Optional.ofNullable(result)
    }

    override fun findAllSchedule(
        scheduleIds: List<Long>,
        reservationStatus: ReservationStatus,
        trainerId: Long
    ): List<Schedule> {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .where(
                scheduleIdIn(scheduleIds),
                trainerIdEq(trainerId),
                reservationStatusEq(reservationStatus)
            )
            .fetch()
    }

    override fun findAllSchedule(
        scheduleIds: List<Long>,
        reservationStatus: List<ReservationStatus>,
        trainerId: Long
    ): List<Schedule> {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .where(
                scheduleIdIn(scheduleIds),
                trainerIdEq(trainerId),
                reservationStatusIn(reservationStatus)
            )
            .fetch()
    }

    override fun findAllSchedule(scheduleId: Long, reservationStatus: ReservationStatus, trainerId: Long): Schedule? {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .where(
                scheduleIdEq(scheduleId),
                trainerIdEq(trainerId),
                reservationStatusEq(reservationStatus)
            )
            .fetchOne()
    }

    override fun findAllSchedule(scheduleId: Long, trainerId: Long): Schedule? {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.scheduleWaiting).fetchJoin()
            .where(
                scheduleIdEq(scheduleId),
                trainerIdEq(trainerId),
                reservationStatusEq(COMPLETED)
            )
            .fetchOne()
    }

    override fun findAllDisabledSchedule(lessonStartDt: LocalDate, lessonEndDt: LocalDate): List<Schedule?> {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .where(
                lessonDtBetween(lessonStartDt, lessonEndDt),
                reservationStatusEq(DISABLED)
            )
            .fetch()
    }

    override fun findAllUnwrittenLessonHistory(
        request: UnwrittenLessonHistorySearchCond,
        memberId: Long
    ): List<Schedule> {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.lessonHistories, lessonHistory).fetchJoin()
            .where(
                trainerIdEq(memberId),
                schedule.applicant.isNotNull,
                reservationStatusEq(COMPLETED),
                lessonDateTimeEq(request.lessonDate),
                applicantIdEq(request.studentId)
            )
            .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
            .fetch()
    }

    private fun applicantIdEq(studentId: Long?): BooleanExpression? {
        return studentId?.let {
            schedule.applicant.id.eq(studentId)
        } ?: null
    }

    override fun findAllSimpleLessonHistoryByMemberId(studentId: Long, trainerId: Long): List<Schedule> {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.lessonHistories, lessonHistory).fetchJoin()
            .where(
                schedule.applicant.id.eq(studentId),
                trainerIdEq(trainerId),
                schedule.applicant.isNotNull,
                reservationStatusEq(COMPLETED)
            )
            .orderBy(
                schedule.lessonDt.asc(),
                schedule.lessonStartTime.asc()
            )
            .fetch()
    }
    private fun scheduleIdIn(scheduleIds: List<Long>): BooleanExpression? =
        schedule.id.`in`(scheduleIds)

    private fun lessonEndDtAfter(startTime: LocalTime?): BooleanExpression? =
        schedule.lessonEndTime.after(startTime)

    private fun lessonStartDtBefore(endTime: LocalTime?): BooleanExpression? =
        schedule.lessonStartTime.before(endTime)

    private fun lessonDtMonthEq(lessonDt: LocalDate): BooleanExpression? =
        schedule.lessonDt.eq(lessonDt)

    private fun applicantIsNotNull(): BooleanExpression? =
        schedule.applicant.isNotNull

    private fun reservationStatusEq(reservationStatus: ReservationStatus): BooleanExpression? =
        schedule.reservationStatus.eq(reservationStatus)

    private fun reservationStatusIn(reservationStatus: List<ReservationStatus>): BooleanExpression? =
        schedule.reservationStatus.`in`(reservationStatus)

    private fun scheduleIdEq(scheduleId: Long): BooleanExpression? =
        schedule.id.eq(scheduleId)

    private fun trainerIdEq(trainerId: Long): BooleanExpression? =
        schedule.trainer.id.eq(trainerId)

    private fun lessonDtBetween(lessonStartDt: LocalDate?, lessonEndDt: LocalDate?): BooleanExpression? {
        if (!ObjectUtils.isEmpty(lessonStartDt) && !ObjectUtils.isEmpty(lessonEndDt)) {
            return schedule.lessonDt.between(lessonStartDt, lessonEndDt)
        }
        return null
    }

    private fun lessonDtMonthEq(lessonDt: String?): BooleanExpression? {
        if (!ObjectUtils.isEmpty(lessonDt)) {
            val formattedDate = stringTemplate("DATE_FORMAT({0}, '%Y-%m')", schedule.lessonDt)
            return formattedDate.eq(lessonDt)
        }
        return null
    }

    private fun lessonDtEq(lessonDt: String): BooleanExpression? {
        val formattedDate = stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", schedule.lessonDt)
        return formattedDate.eq(lessonDt)
    }

    private fun lessonDateTimeEq(lessonDate: String?): BooleanExpression? {
        return lessonDate?.let {
            val formattedDate = stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", schedule.lessonDt)
            return formattedDate.eq(lessonDate)
        } ?: null
    }
}


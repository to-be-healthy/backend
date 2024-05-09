package com.tobe.healthy.schedule.repository.trainer

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions.stringTemplate
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.member.domain.entity.QMember
import com.tobe.healthy.schedule.domain.entity.QSchedule.schedule
import com.tobe.healthy.schedule.domain.entity.QScheduleWaiting.scheduleWaiting
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED
import com.tobe.healthy.schedule.domain.entity.Schedule
import com.tobe.healthy.schedule.entity.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.entity.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.entity.`in`.TrainerTodayScheduleSearchCond
import com.tobe.healthy.schedule.entity.out.LessonResponse
import com.tobe.healthy.schedule.entity.out.TrainerTodayScheduleResponse
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
        searchCond: ScheduleSearchCond,
        trainerId: Long
    ): LessonResponse? {
        val results = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(schedule.applicant, QMember("applicant")).fetchJoin()
            .leftJoin(schedule.scheduleWaiting, scheduleWaiting)
            .on(scheduleWaitingDelYnEq(false))
            .where(
                lessonDtMonthEq(searchCond.lessonDt),
                lessonDtBetween(searchCond),
                trainerIdEq(trainerId),
                delYnEq(false)
            )
            .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
            .fetch()

        return LessonResponse.from(results)
    }

    override fun findOneTrainerTodaySchedule(
        searchCond: TrainerTodayScheduleSearchCond,
        trainerId: Long
    ): TrainerTodayScheduleResponse? {
        val results = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(schedule.applicant, QMember("applicant")).fetchJoin()
            .where(
                lessonDtEq(searchCond.lessonDt),
                trainerIdEq(trainerId),
                reservationStatusEq(COMPLETED),
                delYnEq(false)
            )
            .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
            .fetch()

        val response = LessonResponse.from(results)

        response?.let {
            val trainerTodaySchedule = TrainerTodayScheduleResponse(trainerName = response.trainerName)

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

        } ?: return null
    }

    override fun findAvailableRegisterSchedule(request: RegisterScheduleCommand, trainerId: Long): Schedule? {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .where(
                lessonDtMonthEq(request),
                lessonStartDtBefore(request.lessonEndTime),
                lessonEndDtAfter(request.lessonStartTime),
                trainerIdEq(trainerId),
            )
            .fetchOne()
    }

    override fun validateRegisterSchedule(
        lessonDt: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
        trainerId: Long,
    ): Long {
        return queryFactory
            .select(schedule.count())
            .from(schedule)
            .where(
                lessonDtMonthEq(lessonDt),
                trainerIdEq(trainerId),
                lessonStartDtBefore(endTime),
                lessonEndDtAfter(startTime)
            )
            .fetchOne()!!
    }

    override fun findAvailableWaitingId(scheduleId: Long): Optional<Schedule> {
        val result = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.scheduleWaiting, scheduleWaiting)
            .where(
                scheduleIdEq(scheduleId),
                reservationStatusEq(COMPLETED),
                applicantIsNotNull(),
                delYnEq(false)
            )
            .fetchOne()
        return Optional.ofNullable(result)
    }

    override fun findScheduleByTrainerId(
        scheduleId: Long,
        reservationStatus: ReservationStatus,
        trainerId: Long
    ): Schedule? {
        return queryFactory.select(schedule).from(schedule)
            .where(
                scheduleIdEq(scheduleId),
                trainerIdEq(trainerId),
                reservationStatusEq(reservationStatus),
                delYnEq(false)
            )
            .fetchOne()
    }

    override fun findScheduleByTrainerId(scheduleId: Long, trainerId: Long): Schedule? {
        return queryFactory.select(schedule).from(schedule)
            .where(
                scheduleIdEq(scheduleId),
                trainerIdEq(trainerId),
                delYnEq(false)
            )
            .fetchOne()
    }

    override fun findAllByLessonDtAndTrainerId(lessonDt: String, trainerId: Long): List<Schedule?> {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.scheduleWaiting).fetchJoin()
            .where(
                lessonDtEq(lessonDt),
                trainerIdEq(trainerId),
                delYnEq(false)
            )
            .fetch()
    }

    private fun lessonEndDtAfter(startTime: LocalTime?): BooleanExpression? =
        schedule.lessonEndTime.after(startTime)

    private fun lessonStartDtBefore(endTime: LocalTime?): BooleanExpression? =
        schedule.lessonStartTime.before(endTime)

    private fun scheduleWaitingDelYnEq(boolean: Boolean): BooleanExpression? =
        scheduleWaiting.delYn.eq(boolean)

    private fun lessonDtMonthEq(request: RegisterScheduleCommand): BooleanExpression? =
        schedule.lessonDt.eq(request.lessonDt)

    private fun lessonDtMonthEq(lessonDt: LocalDate): BooleanExpression? =
        schedule.lessonDt.eq(lessonDt)

    private fun applicantIsNotNull(): BooleanExpression? =
        schedule.applicant.isNotNull

    private fun reservationStatusEq(reservationStatus: ReservationStatus): BooleanExpression? =
        schedule.reservationStatus.eq(reservationStatus)

    private fun delYnEq(boolean: Boolean): BooleanExpression? =
        schedule.delYn.eq(boolean)

    private fun scheduleIdEq(scheduleId: Long): BooleanExpression? =
        schedule.id.eq(scheduleId)

    private fun trainerIdEq(trainerId: Long): BooleanExpression? =
        schedule.trainer.id.eq(trainerId)

    private fun lessonDtBetween(searchCond: ScheduleSearchCond): BooleanExpression? {
        if (!ObjectUtils.isEmpty(searchCond.lessonStartDt) && !ObjectUtils.isEmpty(searchCond.lessonEndDt)) {
            return schedule.lessonDt.between(searchCond.lessonStartDt, searchCond.lessonEndDt)
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
}
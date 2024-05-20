package com.tobe.healthy.schedule.repository.trainer

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions.stringTemplate
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.member.domain.entity.QMember
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterIndividualSchedule
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
        retrieveTrainerScheduleByLessonInfo: RetrieveTrainerScheduleByLessonInfo,
        trainerId: Long
    ): RetrieveTrainerScheduleByLessonInfoResult? {
        val results = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(schedule.applicant, QMember("applicant")).fetchJoin()
            .leftJoin(schedule.scheduleWaiting, scheduleWaiting)
            .on(scheduleWaitingDelYnEq(false))
            .where(
                lessonDtMonthEq(retrieveTrainerScheduleByLessonInfo.lessonDt),
                lessonDtBetween(retrieveTrainerScheduleByLessonInfo.lessonStartDt, retrieveTrainerScheduleByLessonInfo.lessonEndDt),
                trainerIdEq(trainerId),
                delYnEq(false)
            )
            .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
            .fetch()

        return RetrieveTrainerScheduleByLessonInfoResult.from(results)
    }

    override fun findOneTrainerTodaySchedule(
        queryTrainerSchedule: RetrieveTrainerScheduleByLessonDt,
        trainerId: Long
    ): RetrieveTrainerScheduleByLessonDtResult? {
        val results = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(schedule.applicant, QMember("applicant")).fetchJoin()
            .where(
                lessonDtEq(queryTrainerSchedule.lessonDt),
                trainerIdEq(trainerId),
                reservationStatusEq(COMPLETED),
                delYnEq(false)
            )
            .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
            .fetch()

        val scheduleCount = queryFactory
            .select(schedule.count())
            .from(schedule)
            .where(
                lessonDtEq(queryTrainerSchedule.lessonDt),
                trainerIdEq(trainerId),
                reservationStatusEq(COMPLETED),
                delYnEq(false)
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

    override fun findAvailableRegisterSchedule(request: CommandRegisterIndividualSchedule, trainerId: Long): Schedule? {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .where(
                lessonDtMonthEq(request),
                lessonStartDtBefore(request.lessonEndTime),
                lessonEndDtAfter(request.lessonStartTime),
                trainerIdEq(trainerId),
                delYnEq(false)
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
                lessonEndDtAfter(startTime),
                delYnEq(false)
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
        return queryFactory
            .select(schedule)
            .from(schedule)
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

    private fun lessonEndDtAfter(startTime: LocalTime?): BooleanExpression? =
        schedule.lessonEndTime.after(startTime)

    private fun lessonStartDtBefore(endTime: LocalTime?): BooleanExpression? =
        schedule.lessonStartTime.before(endTime)

    private fun scheduleWaitingDelYnEq(boolean: Boolean): BooleanExpression? =
        scheduleWaiting.delYn.eq(boolean)

    private fun lessonDtMonthEq(request: CommandRegisterIndividualSchedule): BooleanExpression? =
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
}
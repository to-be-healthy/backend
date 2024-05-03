package com.tobe.healthy.schedule.repository.trainer

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions.stringTemplate
import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.QMember
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.domain.dto.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.entity.QSchedule.schedule
import com.tobe.healthy.schedule.domain.entity.QStandBySchedule.standBySchedule
import com.tobe.healthy.schedule.domain.entity.Schedule
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import java.time.LocalDate
import java.time.LocalTime

@Repository
@Slf4j
class TrainerScheduleRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TrainerScheduleRepositoryCustom {

    override fun findAllSchedule(searchCond: ScheduleSearchCond, trainerId: Long, member: Member, ): List<ScheduleCommandResult> {
        val results = queryFactory
            .select(schedule)
            .from(schedule)
            .leftJoin(schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(schedule.applicant, QMember("applicant")).fetchJoin()
            .leftJoin(schedule.standBySchedule, standBySchedule)
            .on(standBySchedule.delYn.isFalse())
            .where(
                lessonDtEq(searchCond),
                lessonDtBetween(searchCond),
                delYnFalse(),
                schedule.trainer.id.eq(trainerId))
            .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
            .fetch()

        return results.map { ScheduleCommandResult.from(it, member) }
    }

    private fun delYnFalse(): BooleanExpression {
        return schedule.delYn.eq(false)
    }

    override fun findAvailableRegisterSchedule(request: RegisterScheduleCommand, trainerId: Long): Schedule? {
        return queryFactory
            .select(schedule)
            .from(schedule)
            .where(
                schedule.lessonDt.eq(request?.lessonDt),
                schedule.lessonStartTime.between(request?.lessonStartTime, request?.lessonEndTime),
                schedule.lessonEndTime.between(request?.lessonStartTime, request?.lessonEndTime),
                schedule.trainer.id.eq(trainerId),
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
                schedule.lessonDt.eq(lessonDt),
                schedule.trainer.id.eq(trainerId),
                schedule.lessonStartTime.before(endTime).and(schedule.lessonEndTime.after(startTime)))
            .fetchOne()!!
    }

    private fun lessonDtBetween(searchCond: ScheduleSearchCond): BooleanExpression? {
        if (!ObjectUtils.isEmpty(searchCond.lessonStartDt) && !ObjectUtils.isEmpty(searchCond.lessonEndDt)) {
            return schedule.lessonDt.between(searchCond.lessonStartDt, searchCond.lessonEndDt)
        }
        return null
    }

    private fun lessonDtEq(searchCond: ScheduleSearchCond): BooleanExpression? {
        if (!ObjectUtils.isEmpty(searchCond.lessonDt)) {
            val formattedDate: StringExpression = stringTemplate("DATE_FORMAT({0}, '%Y%m')", schedule.lessonDt)
            return formattedDate.eq(searchCond.lessonDt)
        }
        return null
    }
}

package com.tobe.healthy.schedule.repository.trainer

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.QMember
import com.tobe.healthy.schedule.domain.dto.`in`.RegisterScheduleCommand
import com.tobe.healthy.schedule.domain.dto.`in`.ScheduleSearchCond
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult
import com.tobe.healthy.schedule.domain.entity.QSchedule
import com.tobe.healthy.schedule.domain.entity.QStandBySchedule
import com.tobe.healthy.schedule.domain.entity.Schedule
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import java.time.LocalDate
import java.time.LocalTime
import java.util.Optional
import java.util.stream.Collectors.toList

@Repository
@Slf4j
class TrainerScheduleRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TrainerScheduleRepositoryCustom {

    override fun findAllSchedule(
        searchCond: ScheduleSearchCond,
        trainerId: Long?,
        member: Member?,
    ): List<ScheduleCommandResult> {
        val results = queryFactory
            .select(QSchedule.schedule)
            .from(QSchedule.schedule)
            .leftJoin(QSchedule.schedule.trainer, QMember("trainer")).fetchJoin()
            .leftJoin(QSchedule.schedule.applicant, QMember("applicant")).fetchJoin()
            .leftJoin(QSchedule.schedule.standBySchedule, QStandBySchedule.standBySchedule)
            .on(QStandBySchedule.standBySchedule.delYn.isFalse())
            .where(
                lessonDtEq(searchCond),
                lessonDtBetween(searchCond),
                delYnFalse(),
                QSchedule.schedule.trainer.id.eq(trainerId),
            )
            .orderBy(QSchedule.schedule.lessonDt.asc(), QSchedule.schedule.lessonStartTime.asc())
            .fetch()

        return results.stream()
            .map { result: Schedule? ->
                ScheduleCommandResult.from(
                    result,
                    member,
                )
            }
            .collect(toList())
    }

    private fun delYnFalse(): BooleanExpression {
        return QSchedule.schedule.delYn.eq(false)
    }

    override fun findAvailableRegisterSchedule(request: RegisterScheduleCommand, trainerId: Long?): Optional<Schedule> {
        val entity = queryFactory
            .select(QSchedule.schedule)
            .from(QSchedule.schedule)
            .where(
                QSchedule.schedule.lessonDt.eq(request.lessonDt),
                QSchedule.schedule.lessonStartTime.between(request.lessonStartTime, request.lessonEndTime),
                QSchedule.schedule.lessonEndTime.between(request.lessonStartTime, request.lessonEndTime),
                QSchedule.schedule.trainer.id.eq(trainerId),
            )
            .fetchOne()
        return Optional.ofNullable(entity)
    }

    override fun validateRegisterSchedule(
        lessonDt: LocalDate?,
        startTime: LocalTime?,
        endTime: LocalTime?,
        trainerId: Long?,
    ): Boolean? {
        return queryFactory.select(QSchedule.schedule.count().gt(0).`as`("isScheduleRegisterd"))
            .from(QSchedule.schedule)
            .where(
                QSchedule.schedule.lessonDt.eq(lessonDt),
                QSchedule.schedule.trainer.id.eq(trainerId),
                (QSchedule.schedule.lessonStartTime.between(startTime, endTime)
                    .or(QSchedule.schedule.lessonEndTime.between(startTime, endTime))),
            )
            .fetchOne()
    }

    private fun lessonDtBetween(searchCond: ScheduleSearchCond): BooleanExpression? {
        if (!ObjectUtils.isEmpty(searchCond.lessonStartDt) && !ObjectUtils.isEmpty(searchCond.lessonEndDt)) {
            return QSchedule.schedule.lessonDt.between(searchCond.lessonStartDt, searchCond.lessonEndDt)
        }
        return null
    }

    private fun lessonDtEq(searchCond: ScheduleSearchCond): BooleanExpression? {
        if (!ObjectUtils.isEmpty(searchCond.lessonDt)) {
            val formattedDate: StringExpression =
                Expressions.stringTemplate("DATE_FORMAT({0}, '%Y%m')", QSchedule.schedule.lessonDt)
            return formattedDate.eq(searchCond.lessonDt)
        }
        return null
    }
}

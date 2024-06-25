package com.tobe.healthy.lessonhistory.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RetrieveLessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonhistory.domain.entity.QLessonHistory.lessonHistory
import com.tobe.healthy.lessonhistory.domain.entity.QLessonHistoryFiles.lessonHistoryFiles
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.schedule.domain.entity.QSchedule.schedule
import io.micrometer.common.util.StringUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class LessonHistoryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : LessonHistoryRepositoryCustom {

    override fun findById(lessonHistoryId: Long, memberId: Long, memberType: MemberType): LessonHistory? {
        return queryFactory
            .select(lessonHistory)
            .from(lessonHistory)
            .where(
                lessonHistory.id.eq(lessonHistoryId),
                validateMemberTypeAndMemberIdEq(memberId, memberType)
            )
            .fetchOne()
    }

    override fun findAllLessonHistory(
        request: RetrieveLessonHistoryByDateCond,
        memberId: Long,
        memberType: MemberType
    ): List<LessonHistory> {
        return queryFactory
            .select(lessonHistory)
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule, schedule).fetchJoin()
            .where(
                convertDateFormat(request.searchDate),
                validateMemberTypeAndMemberIdEq(memberId, memberType),
            )
            .orderBy(
                schedule.lessonDt.desc(),
                schedule.lessonStartTime.desc(),
                lessonHistory.id.desc()
            )
            .limit(50)
            .fetch()
    }

    override fun findOneLessonHistory(
        lessonHistoryId: Long,
        memberId: Long,
        memberType: MemberType
    ): LessonHistory? {
        return queryFactory
            .selectDistinct(lessonHistory)
            .from(lessonHistory)
            .leftJoin(lessonHistory.lessonHistoryComment).fetchJoin()
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .where(
                validateMemberTypeAndMemberIdEq(memberId, memberType),
                lessonHistoryIdEq(lessonHistoryId)
            )
            .fetchOne()
        }

    private fun lessonHistoryIdEq(lessonHistoryId: Long): BooleanExpression? =
        lessonHistory.id.eq(lessonHistoryId)

    override fun findAllLessonHistoryByMemberId(
        studentId: Long,
        request: RetrieveLessonHistoryByDateCond,
        trainerId: Long
    ): List<LessonHistory> {
        return queryFactory
            .select(lessonHistory)
            .from(lessonHistory)
            .leftJoin(lessonHistory.trainer).fetchJoin()
            .leftJoin(lessonHistory.student).fetchJoin()
            .leftJoin(lessonHistory.schedule).fetchJoin()
            .where(
                convertDateFormat(request.searchDate),
                lessonHistory.student.id.eq(studentId),
                lessonHistory.trainer.id.eq(trainerId)
            )
            .orderBy(
                schedule.lessonDt.desc(),
                schedule.lessonStartTime.desc(),
                lessonHistory.id.desc()
            )
            .limit(50)
            .fetch()
    }

    override fun findTop1LessonHistoryByMemberId(studentId: Long): RetrieveLessonHistoryByDateCondResult? {
        val entity = queryFactory
            .select(lessonHistory)
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .where(lessonHistory.student.id.eq(studentId))
            .orderBy(lessonHistory.schedule.lessonDt.desc())
            .limit(1)
            .fetchOne()

        return RetrieveLessonHistoryByDateCondResult.top1From(entity)
    }

    override fun findAllMyLessonHistory(
        request: RetrieveLessonHistoryByDateCond,
        pageable: Pageable,
        member: CustomMemberDetails
    ): Page<LessonHistory> {
        val results = queryFactory
            .selectDistinct(lessonHistory)
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .leftJoin(lessonHistory.files, lessonHistoryFiles).fetchJoin()
            .where(
                convertDateFormat(request.searchDate),
                validateMemberTypeAndMemberIdEq(member.memberId, member.memberType),
                lessonHistoryFiles.lessonHistoryComment.id.isNull
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(
                schedule.lessonDt.desc(),
                schedule.lessonStartTime.desc(),
                lessonHistory.id.desc()
            )
            .fetch()

        val totalCount = queryFactory
            .select(lessonHistory.count())
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer)
            .innerJoin(lessonHistory.student)
            .innerJoin(lessonHistory.schedule)
            .leftJoin(lessonHistory.files, lessonHistoryFiles)
            .where(
                convertDateFormat(request.searchDate),
                validateMemberTypeAndMemberIdEq(member.memberId, member.memberType),
                lessonHistoryFiles.lessonHistoryComment.id.isNull
            )

        return PageableExecutionUtils.getPage(results, pageable) { totalCount.fetchOne() ?: 0L }
    }

    override fun findOneLessonHistoryWithFiles(
        lessonHistoryId: Long,
        trainerId: Long
    ): LessonHistory? {
        return queryFactory
            .selectDistinct(lessonHistory)
            .from(lessonHistory)
            .leftJoin(lessonHistory.files).fetchJoin()
            .where(
                lessonHistory.id.eq(lessonHistoryId),
                lessonHistory.trainer.id.eq(trainerId)
            )
            .fetchOne()
    }

    override fun validateDuplicateLessonHistory(trainerId: Long, studentId: Long, scheduleId: Long): Boolean {
        val count = queryFactory
            .select(lessonHistory.count())
            .from(lessonHistory)
            .where(
                lessonHistory.trainer.id.eq(trainerId),
                lessonHistory.student.id.eq(studentId),
                lessonHistory.schedule.id.eq(scheduleId)
            )
            .fetchOne() ?: 0
        return count > 0
    }

    private fun validateMemberTypeAndMemberIdEq(memberId: Long, memberType: MemberType): BooleanExpression? {
        return when (memberType) {
            TRAINER -> {
                lessonHistory.trainer.id.eq(memberId)
            }
            else -> {
                lessonHistory.student.id.eq(memberId)
            }
        }
    }

    private fun convertDateFormat(searchDate: String?): BooleanExpression? {
        if (StringUtils.isEmpty(searchDate)) {
            return null
        }
        val firstDt = LocalDate.parse("${searchDate}-01")
        val lastDt = firstDt.withDayOfMonth(firstDt.lengthOfMonth())

        return lessonHistory.schedule.lessonDt.between(firstDt, lastDt)
    }
}


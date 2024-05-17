package com.tobe.healthy.lessonhistory.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions.stringTemplate
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.LESSON_HISTORY_NOT_FOUND
import com.tobe.healthy.lessonhistory.domain.dto.`in`.SearchCondRequest
import com.tobe.healthy.lessonhistory.domain.dto.out.LessonHistoryDetailResponse
import com.tobe.healthy.lessonhistory.domain.dto.out.LessonHistoryResponse
import com.tobe.healthy.lessonhistory.domain.entity.FeedbackCheckStatus.READ
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonhistory.domain.entity.QLessonHistory.lessonHistory
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import io.micrometer.common.util.StringUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class LessonHistoryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : LessonHistoryRepositoryCustom {

    override fun findAllLessonHistory(request: SearchCondRequest, pageable: Pageable, memberId: Long, memberType: MemberType): Page<LessonHistoryResponse> {
        val entities = queryFactory
            .select(lessonHistory)
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .where(convertDateFormat(request.searchDate), validateMemberType(memberId, memberType))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val contents = entities.map { LessonHistoryResponse.from(it) }.toMutableList()

        val totalCount = queryFactory
            .select(lessonHistory.count())
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer)
            .innerJoin(lessonHistory.student)
            .innerJoin(lessonHistory.schedule)
            .where(convertDateFormat(request.searchDate), validateMemberType(memberId, memberType))

        return PageableExecutionUtils.getPage(contents, pageable) { totalCount.fetchOne() ?: 0L }
    }

    override fun findOneLessonHistory(lessonHistoryId: Long, memberId: Long, memberType: MemberType): LessonHistoryDetailResponse? {
        val entity = queryFactory
            .selectDistinct(lessonHistory)
            .from(lessonHistory)
            .leftJoin(lessonHistory.lessonHistoryComment).fetchJoin()
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .where(lessonHistory.id.eq(lessonHistoryId), validateMemberType(memberId, memberType))
            .fetchOne()

        entity?.let {
            updateFeedbackCheckStatus(entity, memberId)
        } ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        return LessonHistoryDetailResponse.detailFrom(entity)
    }

    override fun findAllLessonHistoryByMemberId(studentId: Long, request: SearchCondRequest, pageable: Pageable): Page<LessonHistoryResponse> {
        val entities = queryFactory
            .select(lessonHistory)
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .where(convertDateFormat(request.searchDate), lessonHistory.student.id.eq(studentId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val contents = entities.map {LessonHistoryResponse.from(it)}.toMutableList()

        val totalCount = queryFactory
            .select(lessonHistory.count())
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer)
            .innerJoin(lessonHistory.student)
            .innerJoin(lessonHistory.schedule)
            .where(convertDateFormat(request.searchDate), lessonHistory.student.id.eq(studentId))

        return PageableExecutionUtils.getPage(contents, pageable) { totalCount.fetchOne() ?: 0L }
    }

    override fun findTop1LessonHistoryByMemberId(studentId: Long): LessonHistoryResponse? {
        val entity = queryFactory
            .select(lessonHistory)
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .where(lessonHistory.student.id.eq(studentId))
            .orderBy(lessonHistory.createdAt.desc())
            .limit(1)
            .fetchOne()

        return LessonHistoryResponse.from(entity)
    }

    private fun updateFeedbackCheckStatus(
        results: LessonHistory,
        memberId: Long,
    ) {
        if (results.student.id.equals(memberId)) {
            results.updateFeedbackStatus(READ)
        }
    }

    private fun validateMemberType(memberId: Long, memberType: MemberType): BooleanExpression {
        return if (memberType == TRAINER) {
            lessonHistory.trainer.id.eq(memberId)
        } else {
            lessonHistory.student.id.eq(memberId)
        }
    }

    private fun convertDateFormat(searchDate: String?): BooleanExpression? {
        if (StringUtils.isEmpty(searchDate)) {
            return null
        }
        val stringTemplate = stringTemplate("DATE_FORMAT({0}, '%Y-%m')", lessonHistory.schedule.lessonDt)
        return stringTemplate.eq(searchDate)
    }
}


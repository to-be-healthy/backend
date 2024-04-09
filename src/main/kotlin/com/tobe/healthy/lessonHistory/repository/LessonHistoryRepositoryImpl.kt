package com.tobe.healthy.lessonHistory.repository

import com.querydsl.core.types.ConstantImpl.create
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions.stringTemplate
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.LESSON_HISTORY_NOT_FOUND
import com.tobe.healthy.file.repository.FileRepository
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult
import com.tobe.healthy.lessonHistory.domain.dto.SearchCondRequest
import com.tobe.healthy.lessonHistory.domain.entity.QLessonHistory.lessonHistory
import io.micrometer.common.util.StringUtils
import org.springframework.stereotype.Repository
import java.util.stream.Collectors.toList

@Repository
class LessonHistoryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val fileRepository: FileRepository
) : LessonHistoryRepositoryCustom {

    override fun findAllLessonHistory(request: SearchCondRequest): List<LessonHistoryCommandResult> {
        val entity = queryFactory
            .selectDistinct(lessonHistory)
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .leftJoin(lessonHistory.lessonHistoryComment).fetchJoin()
            .where(convertDateFormat(request.searchDate))
            .fetch() ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        return entity.stream().map { e -> LessonHistoryCommandResult.from(e) }.collect(toList())
    }

    private fun convertDateFormat(searchDate: String?): BooleanExpression? {
        if (StringUtils.isEmpty(searchDate)) {
            return null;
        }
        val stringTemplate = stringTemplate(
            "DATE_FORMAT({0}, {1})",
            lessonHistory.schedule.lessonDt,
            create("%Y%m")
        )
        return stringTemplate.eq(searchDate)
    }

    override fun findOneLessonHistory(lessonHistoryId: Long): List<LessonHistoryCommandResult> {
        val entity = queryFactory
            .selectDistinct(lessonHistory)
            .from(lessonHistory)
            .leftJoin(lessonHistory.lessonHistoryComment).fetchJoin()
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .where(lessonHistory.id.eq(lessonHistoryId))
            .fetch() ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)

        return entity.stream().map { entity -> LessonHistoryCommandResult.from(entity) }.collect(toList())
    }
}


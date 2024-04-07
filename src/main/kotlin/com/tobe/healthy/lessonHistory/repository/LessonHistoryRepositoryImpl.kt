package com.tobe.healthy.lessonHistory.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.LESSON_HISTORY_NOT_FOUND
import com.tobe.healthy.file.repository.FileRepository
import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult
import com.tobe.healthy.lessonHistory.domain.entity.QLessonHistory.lessonHistory
import org.springframework.stereotype.Repository
import java.util.stream.Collectors.toList

@Repository
class LessonHistoryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val fileRepository: FileRepository
) : LessonHistoryRepositoryCustom {

    override fun findAllLessonHistory(): List<LessonHistoryCommandResult> {
        val entity = queryFactory
            .selectDistinct(lessonHistory)
            .from(lessonHistory)
            .innerJoin(lessonHistory.trainer).fetchJoin()
            .innerJoin(lessonHistory.student).fetchJoin()
            .innerJoin(lessonHistory.schedule).fetchJoin()
            .leftJoin(lessonHistory.lessonHistoryComment).fetchJoin()
            .fetch()

        entity.ifEmpty {
            throw CustomException(LESSON_HISTORY_NOT_FOUND)
        }

        val collect = entity.stream().map { e -> e.id }.collect(toList())

        val files = fileRepository.findAllByLessonHistoryIdIn(collect)

        return entity.stream().map { entity -> LessonHistoryCommandResult.from(entity, files) }.collect(toList())
    }

    override fun findOneLessonHistory(lessonHistoryId: Long): LessonHistoryCommandResult {
        val entity = queryFactory
            .selectDistinct(lessonHistory)
            .from(lessonHistory)
            .where(lessonHistory.id.eq(lessonHistoryId))
            .leftJoin(lessonHistory.lessonHistoryComment).fetchJoin()
            .fetchOne()

        val files = fileRepository.findByLessonHistoryId(entity?.id)
        return LessonHistoryCommandResult.from(entity!!, files)
    }
}
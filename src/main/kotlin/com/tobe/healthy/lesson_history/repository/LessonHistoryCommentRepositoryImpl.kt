package com.tobe.healthy.lesson_history.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.lesson_history.domain.entity.QLessonHistoryComment.lessonHistoryComment
import org.springframework.stereotype.Repository

@Repository
class LessonHistoryCommentRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : LessonHistoryCommentRepositoryCustom {

    override fun findTopComment(lessonHistoryId: Long): Int {
        val result = queryFactory
            .select(lessonHistoryComment.order.max().add(1))
            .from(lessonHistoryComment)
            .where(lessonHistoryIdEq(lessonHistoryId), parentCommentIdIsNull())
            .fetchOne() ?: 1
        return result;
    }

    override fun findTopComment(lessonHistoryId: Long, lessonHistoryCommentId: Long): Int {
        return queryFactory
            .select(lessonHistoryComment.order.max().add(1))
            .from(lessonHistoryComment)
            .where(
                lessonHistoryIdEq(lessonHistoryId),
                parentCommentIdEq(lessonHistoryCommentId)
            )
            .fetchOne() ?: 1
    }

    private fun parentCommentIdEq(lessonHistoryCommentParentId: Long): BooleanExpression? =
        lessonHistoryComment.parent.id.eq(lessonHistoryCommentParentId)

    private fun parentCommentIdIsNull(): BooleanExpression =
        lessonHistoryComment.parent.isNull

    private fun lessonHistoryIdEq(lessonHistoryId: Long): BooleanExpression =
        lessonHistoryComment.lessonHistory.id.eq(lessonHistoryId)
}

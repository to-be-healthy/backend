package com.tobe.healthy.lessonHistory.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.lessonHistory.domain.entity.QLessonHistoryComment.lessonHistoryComment
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

    override fun findTopComment(lessonHistoryId: Long, lessonHistoryCommentParentId: Long): Int {
        val result = queryFactory
            .select(lessonHistoryComment.order.max().add(1))
            .from(lessonHistoryComment)
            .where(lessonHistoryIdEq(lessonHistoryId), parentCommentIdEq(lessonHistoryCommentParentId))
            .fetchOne() ?: 1
        return result;
    }

    private fun parentCommentIdEq(lessonHistoryCommentParentId: Long): BooleanExpression? =
        lessonHistoryComment.parentId.id.eq(lessonHistoryCommentParentId)

    private fun parentCommentIdIsNull(): BooleanExpression =
        lessonHistoryComment.parentId.isNull

    private fun lessonHistoryIdEq(lessonHistoryId: Long): BooleanExpression =
        lessonHistoryComment.lessonHistory.id.eq(lessonHistoryId)
}

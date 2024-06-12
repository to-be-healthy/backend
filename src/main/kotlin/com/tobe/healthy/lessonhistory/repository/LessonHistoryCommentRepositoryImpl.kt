package com.tobe.healthy.lessonhistory.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment
import com.tobe.healthy.lessonhistory.domain.entity.QLessonHistoryComment.lessonHistoryComment
import org.springframework.stereotype.Repository

@Repository
class LessonHistoryCommentRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : LessonHistoryCommentRepositoryCustom {

    override fun findTopComment(lessonHistoryId: Long?): Int {
        val result = queryFactory
            .select(lessonHistoryComment.order.max().add(1))
            .from(lessonHistoryComment)
            .where(
                lessonHistoryIdEq(lessonHistoryId),
                parentCommentIdIsNull()
            )
            .fetchOne() ?: 1
        return result
    }

    override fun findTopComment(lessonHistoryId: Long?, lessonHistoryCommentId: Long?): Int {
        return queryFactory
            .select(lessonHistoryComment.order.max().add(1))
            .from(lessonHistoryComment)
            .where(
                lessonHistoryIdEq(lessonHistoryId),
                parentCommentIdEq(lessonHistoryCommentId)
            )
            .fetchOne() ?: 1
    }

    override fun findLessonHistoryCommentWithFiles(
        lessonHistoryCommentId: Long,
        writerId: Long
    ): LessonHistoryComment? {
        return queryFactory
            .select(lessonHistoryComment)
            .from(lessonHistoryComment)
            .leftJoin(lessonHistoryComment.files).fetchJoin()
            .where(
                lessonHistoryComment.id.eq(lessonHistoryCommentId),
                lessonHistoryComment.delYn.eq(false),
                lessonHistoryComment.writer.id.eq(writerId)
            )
            .fetchOne()
    }

    override fun findCommentById(lessonHistoryCommentId: Long): LessonHistoryComment? {
        return queryFactory
            .select(lessonHistoryComment)
            .from(lessonHistoryComment)
            .where(
                lessonHistoryComment.id.eq(lessonHistoryCommentId),
                lessonHistoryComment.delYn.eq(false)
            )
            .fetchOne()
    }

    override fun findById(lessonHistoryCommentId: Long, writerId: Long): LessonHistoryComment? {
        return queryFactory
            .select(lessonHistoryComment)
            .from(lessonHistoryComment)
            .where(
                lessonHistoryComment.id.eq(lessonHistoryCommentId),
                lessonHistoryComment.delYn.eq(false),
                lessonHistoryComment.writer.id.eq(writerId)
            )
            .fetchOne()
    }

    private fun parentCommentIdEq(lessonHistoryCommentParentId: Long?): BooleanExpression? =
        lessonHistoryComment.parent.id.eq(lessonHistoryCommentParentId)

    private fun parentCommentIdIsNull(): BooleanExpression =
        lessonHistoryComment.parent.isNull

    private fun lessonHistoryIdEq(lessonHistoryId: Long?): BooleanExpression =
        lessonHistoryComment.lessonHistory.id.eq(lessonHistoryId)
}

package com.tobe.healthy.lessonHistory.repository

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
            .where(lessonHistoryComment.lessonHistory.id.eq(lessonHistoryId), lessonHistoryComment.parentId.isNull)
            .fetchOne() ?: 1
        return result;
    }

    override fun findTopComment(lessonHistoryId: Long, lessonHistoryCommentParentId: Long): Int {
        val result = queryFactory
            .select(lessonHistoryComment.order.max().add(1))
            .from(lessonHistoryComment)
            .where(lessonHistoryComment.lessonHistory.id.eq(lessonHistoryId), lessonHistoryComment.parentId.id.eq(lessonHistoryCommentParentId))
            .fetchOne() ?: 1
        return result;
    }
}

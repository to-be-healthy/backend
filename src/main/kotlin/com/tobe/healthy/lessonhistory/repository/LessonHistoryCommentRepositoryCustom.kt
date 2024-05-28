package com.tobe.healthy.lessonhistory.repository

import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryCommentRepositoryCustom {
    fun findTopComment(lessonHistoryId: Long?): Int
    fun findTopComment(lessonHistoryId: Long?, lessonHistoryCommentId: Long?): Int
}

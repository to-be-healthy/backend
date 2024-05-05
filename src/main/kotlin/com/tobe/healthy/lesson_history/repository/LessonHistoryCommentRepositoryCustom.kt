package com.tobe.healthy.lesson_history.repository

import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryCommentRepositoryCustom {
    fun findTopComment(lessonHistoryId: Long): Int
    fun findTopComment(lessonHistoryId: Long, lessonHistoryCommentId: Long): Int
}

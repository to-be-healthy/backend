package com.tobe.healthy.lessonHistory.repository

import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryCommentRepositoryCustom {
    fun findTopComment(lessonHistoryId: Long): Int?
    fun findTopComment(lessonHistoryId: Long, lessonHistoryCommentParentId: Long): Int?
}
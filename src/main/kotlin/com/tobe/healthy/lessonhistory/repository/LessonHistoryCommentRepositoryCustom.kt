package com.tobe.healthy.lessonhistory.repository

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment
import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryCommentRepositoryCustom {
    fun findTopComment(lessonHistoryId: Long?): Int
    fun findTopComment(lessonHistoryId: Long?, lessonHistoryCommentId: Long?): Int
    fun findLessonHistoryCommentWithFiles(lessonHistoryCommentId: Long): LessonHistoryComment?
    fun findCommentById(lessonHistoryCommentId: Long): LessonHistoryComment?
}

package com.tobe.healthy.lessonHistory.repository

import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryCommentRepository : JpaRepository<LessonHistoryComment, Long>, LessonHistoryCommentRepositoryCustom {
}
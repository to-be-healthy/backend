package com.tobe.healthy.lesson_history.repository

import com.tobe.healthy.lesson_history.domain.entity.LessonHistoryComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryCommentRepository : JpaRepository<LessonHistoryComment, Long>, LessonHistoryCommentRepositoryCustom

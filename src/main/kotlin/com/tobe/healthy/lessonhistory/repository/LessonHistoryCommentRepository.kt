package com.tobe.healthy.lessonhistory.repository

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LessonHistoryCommentRepository : JpaRepository<LessonHistoryComment, Long>, LessonHistoryCommentRepositoryCustom

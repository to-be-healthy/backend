package com.tobe.healthy.lessonhistory.repository

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import org.springframework.data.jpa.repository.JpaRepository

interface LessonHistoryRepository : JpaRepository<LessonHistory, Long>, LessonHistoryRepositoryCustom

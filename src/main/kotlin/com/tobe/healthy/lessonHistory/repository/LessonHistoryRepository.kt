package com.tobe.healthy.lessonHistory.repository

import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import org.springframework.data.jpa.repository.JpaRepository

interface LessonHistoryRepository : JpaRepository<LessonHistory, Long>, LessonHistoryRepositoryCustom {
}

package com.tobe.healthy.lessonHistory.repository

import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LessonHistoryRepository : JpaRepository<LessonHistory, Long>, LessonHistoryRepositoryCustom {
}
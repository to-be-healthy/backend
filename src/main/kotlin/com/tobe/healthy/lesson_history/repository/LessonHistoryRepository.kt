package com.tobe.healthy.lesson_history.repository

import com.tobe.healthy.lesson_history.domain.entity.LessonHistory
import org.springframework.data.jpa.repository.JpaRepository

interface LessonHistoryRepository : JpaRepository<LessonHistory, Long>, LessonHistoryRepositoryCustom

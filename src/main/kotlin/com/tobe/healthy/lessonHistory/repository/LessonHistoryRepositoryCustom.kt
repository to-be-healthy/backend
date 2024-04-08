package com.tobe.healthy.lessonHistory.repository

import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult

interface LessonHistoryRepositoryCustom {
    fun findAllLessonHistory(): List<LessonHistoryCommandResult>
    fun findOneLessonHistory(lessonHistoryId: Long): List<LessonHistoryCommandResult>
}
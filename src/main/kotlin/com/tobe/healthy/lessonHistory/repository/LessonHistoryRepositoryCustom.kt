package com.tobe.healthy.lessonHistory.repository

import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult
import com.tobe.healthy.lessonHistory.domain.dto.SearchCondRequest

interface LessonHistoryRepositoryCustom {
    fun findAllLessonHistory(request: SearchCondRequest): List<LessonHistoryCommandResult>
    fun findOneLessonHistory(lessonHistoryId: Long): List<LessonHistoryCommandResult>
}
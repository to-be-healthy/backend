package com.tobe.healthy.lessonHistory.repository

import com.tobe.healthy.lessonHistory.domain.dto.LessonHistoryCommandResult
import com.tobe.healthy.lessonHistory.domain.dto.SearchCondRequest
import com.tobe.healthy.member.domain.entity.MemberType

interface LessonHistoryRepositoryCustom {
    fun findAllLessonHistory(request: SearchCondRequest, memberId: Long, memberType: MemberType): List<LessonHistoryCommandResult>
    fun findOneLessonHistory(lessonHistoryId: Long, memberId: Long, memberType: MemberType): List<LessonHistoryCommandResult>
}
package com.tobe.healthy.lessonhistory.repository

import com.tobe.healthy.lessonhistory.domain.dto.`in`.LessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.out.LessonHistoryDetailResult
import com.tobe.healthy.lessonhistory.domain.dto.out.LessonHistoryResult
import com.tobe.healthy.member.domain.entity.MemberType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LessonHistoryRepositoryCustom {
    fun findAllLessonHistory(request: LessonHistoryByDateCond, pageable: Pageable, memberId: Long, memberType: MemberType): Page<LessonHistoryResult>
    fun findOneLessonHistory(lessonHistoryId: Long, memberId: Long, memberType: MemberType): LessonHistoryDetailResult?
    fun findAllLessonHistoryByMemberId(studentId: Long, request: LessonHistoryByDateCond, pageable: Pageable): Page<LessonHistoryResult>
    fun findTop1LessonHistoryByMemberId(studentId: Long): LessonHistoryResult?
}

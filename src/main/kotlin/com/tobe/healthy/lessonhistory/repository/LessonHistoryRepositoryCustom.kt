package com.tobe.healthy.lessonhistory.repository

import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RetrieveLessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.member.domain.entity.MemberType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LessonHistoryRepositoryCustom {
    fun findAllLessonHistory(request: RetrieveLessonHistoryByDateCond, pageable: Pageable, memberId: Long, memberType: MemberType): Page<RetrieveLessonHistoryByDateCondResult>
    fun findOneLessonHistory(lessonHistoryId: Long, member: CustomMemberDetails): LessonHistory?
    fun findAllLessonHistoryByMemberId(studentId: Long, request: RetrieveLessonHistoryByDateCond, pageable: Pageable): Page<RetrieveLessonHistoryByDateCondResult>
    fun findTop1LessonHistoryByMemberId(studentId: Long): RetrieveLessonHistoryByDateCondResult?
}

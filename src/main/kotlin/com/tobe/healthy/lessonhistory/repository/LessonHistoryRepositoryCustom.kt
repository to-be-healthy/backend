package com.tobe.healthy.lessonhistory.repository

import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RetrieveLessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.member.domain.entity.MemberType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LessonHistoryRepositoryCustom {
    fun findAllLessonHistory(request: RetrieveLessonHistoryByDateCond, pageable: Pageable, memberId: Long, memberType: MemberType): Page<LessonHistory>
    fun findOneLessonHistory(lessonHistoryId: Long, memberId: Long, memberType: MemberType): LessonHistory?
    fun findAllLessonHistoryByMemberId(studentId: Long, request: RetrieveLessonHistoryByDateCond, trainerId: Long, pageable: Pageable): Page<LessonHistory>
    fun findTop1LessonHistoryByMemberId(studentId: Long): RetrieveLessonHistoryByDateCondResult?
    fun findAllMyLessonHistory(request: RetrieveLessonHistoryByDateCond, pageable: Pageable, member: CustomMemberDetails): Page<LessonHistory>
    fun findOneLessonHistoryWithFiles(lessonHistoryId: Long): LessonHistory?
    fun validateDuplicateLessonHistory(trainerId: Long, studentId: Long, scheduleId: Long): Boolean
}

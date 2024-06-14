package com.tobe.healthy.lessonhistory.repository

import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RetrieveLessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.member.domain.entity.MemberType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LessonHistoryRepositoryCustom {
    fun findById(lessonHistoryId: Long, memberId: Long, memberType: MemberType): LessonHistory?
    fun findAllLessonHistory(request: RetrieveLessonHistoryByDateCond, memberId: Long, memberType: MemberType): List<LessonHistory>
    fun findOneLessonHistory(lessonHistoryId: Long, memberId: Long, memberType: MemberType): LessonHistory?
    fun findAllLessonHistoryByMemberId(studentId: Long, request: RetrieveLessonHistoryByDateCond, trainerId: Long): List<LessonHistory>
    fun findTop1LessonHistoryByMemberId(studentId: Long): RetrieveLessonHistoryByDateCondResult?
    fun findAllMyLessonHistory(request: RetrieveLessonHistoryByDateCond, pageable: Pageable, member: CustomMemberDetails): Page<LessonHistory>
    fun findOneLessonHistoryWithFiles(lessonHistoryId: Long, trainerId: Long): LessonHistory?
    fun validateDuplicateLessonHistory(trainerId: Long, studentId: Long, scheduleId: Long): Boolean
}

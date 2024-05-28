package com.tobe.healthy.lessonhistory.application

import com.tobe.healthy.common.CustomPagingResponse
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.LESSON_HISTORY_NOT_FOUND
import com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RetrieveLessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryDetailResult
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveUnwrittenLessonHistory
import com.tobe.healthy.lessonhistory.repository.LessonHistoryRepository
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LessonHistoryService(
    private val lessonHistoryRepository: LessonHistoryRepository,
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository
) {

    fun findAllLessonHistory(
        request: RetrieveLessonHistoryByDateCond,
        pageable: Pageable,
        memberId: Long,
        memberType: MemberType
    ): CustomPagingResponse<RetrieveLessonHistoryByDateCondResult> {
        val results = lessonHistoryRepository.findAllLessonHistory(request, pageable, memberId, memberType)
        return CustomPagingResponse(
            content = results.content,
            pageNumber = results.pageable.pageNumber,
            pageSize = results.pageable.pageSize,
            totalPages = results.totalPages,
            totalElements = results.totalElements,
            isLast = results.isLast,
        )
    }

    fun findAllLessonHistoryByMemberId(
        studentId: Long,
        request: RetrieveLessonHistoryByDateCond,
        pageable: Pageable
    ): CustomPagingResponse<RetrieveLessonHistoryByDateCondResult> {
        val findMember = memberRepository.findByIdOrNull(studentId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val results = lessonHistoryRepository.findAllLessonHistoryByMemberId(findMember.id, request, pageable)

        return CustomPagingResponse(
            findMember.name,
            results.content,
            results.pageable.pageNumber,
            results.pageable.pageSize,
            results.totalPages,
            results.totalElements,
            results.isLast,
        )
    }

    fun findOneLessonHistory(lessonHistoryId: Long, member: CustomMemberDetails): RetrieveLessonHistoryDetailResult? {
        lessonHistoryRepository.findOneLessonHistory(lessonHistoryId, member)?.let {
            return RetrieveLessonHistoryDetailResult.detailFrom(it)
        } ?: throw CustomException(LESSON_HISTORY_NOT_FOUND)
    }

    fun findAllUnwrittenLessonHistory(memberId: Long): List<RetrieveUnwrittenLessonHistory> {
        val schedules = trainerScheduleRepository.findAllUnwrittenLessonHistory(memberId)
        return schedules.map { RetrieveUnwrittenLessonHistory.from(it) }
    }
}

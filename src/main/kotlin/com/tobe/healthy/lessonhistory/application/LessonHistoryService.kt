package com.tobe.healthy.lessonhistory.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.config.security.CustomMemberDetails
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RetrieveLessonHistoryByDateCond
import com.tobe.healthy.lessonhistory.domain.dto.`in`.UnwrittenLessonHistorySearchCond
import com.tobe.healthy.lessonhistory.domain.dto.out.CustomRetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryByDateCondResult
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveLessonHistoryDetailResult
import com.tobe.healthy.lessonhistory.domain.dto.out.RetrieveUnwrittenLessonHistory
import com.tobe.healthy.lessonhistory.repository.LessonHistoryRepository
import com.tobe.healthy.member.domain.entity.MemberType
import com.tobe.healthy.member.repository.MemberRepository
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class LessonHistoryService(
    private val lessonHistoryRepository: LessonHistoryRepository,
    private val memberRepository: MemberRepository,
    private val trainerScheduleRepository: TrainerScheduleRepository
) {

    fun findAllLessonHistory(
        request: RetrieveLessonHistoryByDateCond,
        member: CustomMemberDetails
    ): CustomRetrieveLessonHistoryByDateCondResult {

        val contents = lessonHistoryRepository.findAllLessonHistory(request, member.memberId, member.memberType)
            .map { lessonHistory -> RetrieveLessonHistoryByDateCondResult.from(lessonHistory) }

        return CustomRetrieveLessonHistoryByDateCondResult.from(contents)
    }

    fun findAllLessonHistoryByMemberId(
        studentId: Long,
        request: RetrieveLessonHistoryByDateCond,
        member: CustomMemberDetails
    ): CustomRetrieveLessonHistoryByDateCondResult {

        val findMember = memberRepository.findByIdOrNull(studentId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val contents = lessonHistoryRepository.findAllLessonHistoryByMemberId(findMember.id, request, member.memberId)
            .map { lessonHistory -> RetrieveLessonHistoryByDateCondResult.from(lessonHistory) }

        return CustomRetrieveLessonHistoryByDateCondResult.from(contents)
    }

    @Transactional
    fun findOneLessonHistory(lessonHistoryId: Long, member: CustomMemberDetails): RetrieveLessonHistoryDetailResult? {
        return lessonHistoryRepository.findOneLessonHistory(lessonHistoryId, member.memberId, member.memberType)
            ?.let {
                if (member.memberType == MemberType.STUDENT) {
                    it.updateFeedbackChecked()
                }

                RetrieveLessonHistoryDetailResult.detailFrom(it)
            }
    }

    fun findAllUnwrittenLessonHistory(
        request: UnwrittenLessonHistorySearchCond,
        memberId: Long
    ): List<RetrieveUnwrittenLessonHistory> {
        return trainerScheduleRepository.findAllUnwrittenLessonHistory(request, memberId)
            .filter { LocalDateTime.now().isAfter(LocalDateTime.of(it.lessonDt, it.lessonEndTime)) }
            .map { RetrieveUnwrittenLessonHistory.from(it) }
    }
}
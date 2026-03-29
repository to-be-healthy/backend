package com.tobe.healthy.lessonhistory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tobe.healthy.config.security.CustomMemberDetails;
import com.tobe.healthy.lessonhistory.presentation.dto.in.RetrieveLessonHistoryByDateCond;
import com.tobe.healthy.lessonhistory.presentation.dto.out.RetrieveLessonHistoryByDateCondResult;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory;
import com.tobe.healthy.member.domain.entity.MemberType;

public interface LessonHistoryRepositoryCustom {
	LessonHistory findById(Long lessonHistoryId, Long memberId, MemberType memberType);

	List<LessonHistory> findAllLessonHistory(RetrieveLessonHistoryByDateCond request, Long memberId,
		MemberType memberType);

	LessonHistory findOneLessonHistory(Long lessonHistoryId, Long memberId, MemberType memberType);

	List<LessonHistory> findAllLessonHistoryByMemberId(Long studentId, RetrieveLessonHistoryByDateCond request,
		Long trainerId);

	RetrieveLessonHistoryByDateCondResult findTop1LessonHistoryByMemberId(Long studentId);

	Page<LessonHistory> findAllMyLessonHistory(RetrieveLessonHistoryByDateCond request, Pageable pageable,
		CustomMemberDetails member);

	LessonHistory findOneLessonHistoryWithFiles(Long lessonHistoryId, Long trainerId);

	boolean validateDuplicateLessonHistory(Long trainerId, Long studentId, Long scheduleId);
}

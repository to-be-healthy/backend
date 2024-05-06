package com.tobe.healthy.schedule.repository.student;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;

import java.util.List;

public interface StudentScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Long trainerId, Member member);
	List<ScheduleCommandResult> findAllByApplicantId(Long memberId);
	List<MyReservation> findAllMyReservation(Long memberId, ScheduleSearchCond searchCond);
	MyReservation findTop1MyReservation(Long memberId);
}

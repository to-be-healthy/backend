package com.tobe.healthy.schedule.repository.student;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.StudentScheduleCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;

import java.util.List;

public interface StudentScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(StudentScheduleCond searchCond, Long trainerId, Member member);
	List<ScheduleCommandResult> findAllByApplicantId(Long memberId);
	List<MyReservation> findNewReservation(Long memberId, StudentScheduleCond searchCond);
	List<MyReservation> findOldReservation(Long memberId, String searchDate);
	MyReservation findMyNextReservation(Long memberId);
	List<String> findMyReservationBlueDot(Long memberId, StudentScheduleCond searchCond);

}

package com.tobe.healthy.schedule.repository.student;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.TrainerSchedule;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;

import java.util.List;

public interface StudentScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(TrainerSchedule searchCond, Long trainerId, Member member);
	List<ScheduleCommandResult> findAllByApplicantId(Long memberId);
	List<MyReservation> findAllMyReservation(Long memberId, TrainerSchedule searchCond);
	MyReservation findMyNextReservation(Long memberId);

}

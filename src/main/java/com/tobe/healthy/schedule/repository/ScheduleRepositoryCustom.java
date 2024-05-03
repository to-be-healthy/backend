package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbySchedule;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Long trainerId, Member member);
	List<ScheduleCommandResult> findAllByApplicantId(Long memberId);
	List<MyReservation> findAllMyReservation(Long memberId, ScheduleSearchCond searchCond);
	List<MyStandbySchedule> findAllMyStandbySchedule(Long memberId);
	Optional<Schedule> findAvailableRegisterSchedule(RegisterScheduleCommand request, Long trainerId);
}

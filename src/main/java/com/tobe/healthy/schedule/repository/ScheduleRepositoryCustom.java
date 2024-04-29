package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservationResponse;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbyScheduleResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Long trainerId);
	List<ScheduleCommandResult> findAllByApplicantId(Long memberId);
	List<MyReservationResponse> findAllMyReservation(Long memberId);
	List<MyStandbyScheduleResponse> findAllMyStandbySchedule(Long memberId);
	Optional<Schedule> findAvailableRegisterSchedule(RegisterScheduleCommand request, Long trainerId);
}

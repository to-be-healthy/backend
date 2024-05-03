package com.tobe.healthy.schedule.repository.trainer;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TrainerScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Long trainerId, Member member);
	Optional<Schedule> findAvailableRegisterSchedule(RegisterScheduleCommand request, Long trainerId);
	Boolean validateRegisterSchedule(LocalDate lessonDt, LocalTime startTime, LocalTime localTime, Long trainerId);
}

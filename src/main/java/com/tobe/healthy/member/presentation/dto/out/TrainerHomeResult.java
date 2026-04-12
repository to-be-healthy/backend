package com.tobe.healthy.member.presentation.dto.out;

import java.util.List;

import com.tobe.healthy.gym.presentation.dto.out.GymDto;
import com.tobe.healthy.member.repository.dto.MemberInTeamResult;
import com.tobe.healthy.schedule.presentation.dto.out.RetrieveTrainerScheduleByLessonDtResult;

public record TrainerHomeResult(
	Long studentCount,
	List<MemberInTeamResult> bestStudents,
	RetrieveTrainerScheduleByLessonDtResult todaySchedule,
	GymDto gym,
	Boolean redDotStatus
) {
}

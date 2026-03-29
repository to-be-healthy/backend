package com.tobe.healthy.member.presentation.dto.out;

import java.util.List;

import com.tobe.healthy.gym.presentation.dto.out.GymDto;
import com.tobe.healthy.schedule.presentation.dto.out.RetrieveTrainerScheduleByLessonDtResult;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class TrainerHomeResult {
	private Long studentCount;
	private List<MemberInTeamResult> bestStudents;
	private RetrieveTrainerScheduleByLessonDtResult todaySchedule;
	private GymDto gym;
	private Boolean redDotStatus;
}

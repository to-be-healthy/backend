package com.tobe.healthy.schedule.presentation.dto.out;

import com.tobe.healthy.schedule.domain.Schedule;

public record ScheduleIdInfo(
	Long studentId,
	Long trainerId,
	Long scheduleId,
	String scheduleTime
) {
	public static ScheduleIdInfo from(Schedule schedule) {
		return new ScheduleIdInfo(
			schedule.getApplicant().getId(),
			schedule.getTrainer().getId(),
			schedule.getId(),
			null
		);
	}

	public static ScheduleIdInfo create(Schedule schedule, String scheduleTime) {
		return new ScheduleIdInfo(
			schedule.getApplicant().getId(),
			schedule.getTrainer().getId(),
			schedule.getId(),
			scheduleTime
		);
	}

	public static ScheduleIdInfo create(Schedule schedule, Long waitingStudentId) {
		return new ScheduleIdInfo(
			waitingStudentId,
			schedule.getTrainer().getId(),
			schedule.getId(),
			null
		);
	}
}

package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleIdInfo {
	private Long studentId;
	private Long trainerId;
	private Long scheduleId;
	private String scheduleTime;

	public static ScheduleIdInfo from(Schedule schedule) {
		return ScheduleIdInfo.builder()
			.studentId(schedule.getApplicant().getId())
			.trainerId(schedule.getTrainer().getId())
			.scheduleId(schedule.getId())
			.build();
	}

	public static ScheduleIdInfo create(Schedule schedule, String scheduleTime) {
		return ScheduleIdInfo.builder()
				.studentId(schedule.getApplicant().getId())
				.trainerId(schedule.getTrainer().getId())
				.scheduleId(schedule.getId())
				.scheduleTime(scheduleTime)
				.build();
	}

	public static ScheduleIdInfo create(Schedule schedule, Long waitingStudentId) {
		return ScheduleIdInfo.builder()
				.studentId(waitingStudentId)
				.trainerId(schedule.getTrainer().getId())
				.scheduleId(schedule.getId())
				.build();
	}
}

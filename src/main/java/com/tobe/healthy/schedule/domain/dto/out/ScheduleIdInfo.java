package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleIdInfo {
	private Long studentId;
	private Long trainerId;
	private Long scheduleId;
	private Long standbyStudentId;

	public static ScheduleIdInfo from(Schedule schedule) {
		return ScheduleIdInfo.builder()
			.studentId(schedule.getApplicant().getId())
			.trainerId(schedule.getTrainer().getId())
			.scheduleId(schedule.getId())
			.build();
	}

	public static ScheduleIdInfo create(Long memberId, Schedule schedule, Long standbyStudentId) {
		return ScheduleIdInfo.builder()
				.studentId(memberId)
				.trainerId(schedule.getTrainer().getId())
				.scheduleId(schedule.getId())
				.standbyStudentId(standbyStudentId)
				.build();
	}

}

package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoShowCommandResponse {
	private Long studentId;
	private Long trainerId;
	private Long scheduleId;

	public static NoShowCommandResponse from(Schedule schedule) {
		return NoShowCommandResponse.builder()
			.studentId(schedule.getApplicant().getId())
			.trainerId(schedule.getTrainer().getId())
			.scheduleId(schedule.getId())
			.build();
	}
}

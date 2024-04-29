package com.tobe.healthy.schedule.domain.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleCommandResponse {

	private List<ScheduleCommandResult> morning;
	private List<ScheduleCommandResult> afternoon;

	public static ScheduleCommandResponse create(List<ScheduleCommandResult> morning, List<ScheduleCommandResult> afternoon) {
		return ScheduleCommandResponse.builder()
				.morning(morning)
				.afternoon(afternoon)
				.build();
	}
}

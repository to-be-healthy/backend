package com.tobe.healthy.schedule.domain.dto.in;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleCommandRequest {
	private Long trainer;
	private Map<String, List<ScheduleRegister>> schedule;

	@Data
	@Builder
	@AllArgsConstructor
	public static class ScheduleRegister {
		private int round;
		private LocalTime startTime;
		private LocalTime endTime;
		private Long applicant;
	}
}


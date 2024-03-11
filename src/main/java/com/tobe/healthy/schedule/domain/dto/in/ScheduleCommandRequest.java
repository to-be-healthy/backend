package com.tobe.healthy.schedule.domain.dto.in;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleCommandRequest {
	private Long trainer;
	private List<ScheduleRegisterInfo> list;

	@Data
	@Builder
	@AllArgsConstructor
	public static class ScheduleRegisterInfo {
		private int round;
		private LocalDateTime startDt;
		private LocalDateTime endDt;
		private Long applicant;
	}
}

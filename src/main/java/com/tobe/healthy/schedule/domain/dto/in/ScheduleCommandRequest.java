package com.tobe.healthy.schedule.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleCommandRequest {
	@Schema(description = "트레이너 아이디")
	@NotNull(message = "트레이너 정보를 입력해 주세요.")
	private Long trainer;

	@Schema(description = "등록할 일정")
	@NotNull(message = "등록할 일정을 입력해 주세요.")
	private Map<String, List<ScheduleRegister>> schedule;

	@Data
	@Builder
	@AllArgsConstructor
	public static class ScheduleRegister {
		@Schema(description = "수업 회차")
		@NotNull(message = "수업 회차를 입력해 주세요.")
		private int round;

		@Schema(description = "수업 시작 시간")
		@NotNull(message = "수업 시작 시간을 입력해 주세요.")
		private LocalTime startTime;

		@Schema(description = "수업 종료 시간")
		@NotNull(message = "수업 종료 시간을 입력해 주세요.")
		private LocalTime endTime;

		@Schema(description = "신청자 아이디")
		private Long applicant;
	}
}


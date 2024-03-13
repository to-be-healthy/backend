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
@Schema(description = "일정 등록 DTO")
public class ScheduleCommandRequest {

	@Schema(description = "트레이너 아이디", example = "1")
	@NotNull(message = "트레이너 정보를 입력해 주세요.")
	private Long trainer;

	@Schema(description = "등록할 일정", example = "{\"2023-03-15\": [{\"round\": 1, \"startTime\": \"10:00\", \"endTime\": \"11:00\", \"applicant\": 1}], \"2023-03-16\": [{\"round\": 2, \"startTime\": \"12:00\", \"endTime\": \"13:00\", \"applicant\": 1}]}")
	@NotNull(message = "등록할 일정을 입력해 주세요.")
	private Map<String, List<ScheduleRegister>> schedule;

	@Data
	@Builder
	@AllArgsConstructor
	public static class ScheduleRegister {
		@NotNull(message = "수업 회차를 입력해 주세요.")
		private int round;

		@NotNull(message = "수업 시작 시간을 입력해 주세요.")
		private LocalTime startTime;

		@NotNull(message = "수업 종료 시간을 입력해 주세요.")
		private LocalTime endTime;

		private Long applicant;
	}
}


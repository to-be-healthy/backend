package com.tobe.healthy.schedule.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "일정 자동생성 DTO")
public class AutoCreateScheduleCommandRequest {

	@Schema(description = "트레이너 아이디")
	@NotNull(message = "트레이너 아이디를 입력해 주세요.")
	private Long trainer;

	@Schema(description = "자동으로 생성할 수업 시작 일자", example = "2024-04-01")
	@NotNull(message = "자동으로 생성할 수업 시작 일자를 입력해 주세요.")
	private LocalDate startDt;

	@Schema(description = "자동으로 생성할 수업 종료 일자", example = "2024-04-30")
	@NotNull(message = "자동으로 생성할 수업 종료 일자를 입력해 주세요.")
	private LocalDate endDt;

	@Schema(description = "자동으로 생성할 평일 수업 시작 시간", example = "10:00:00", type = "string")
	@NotNull(message = "자동으로 생성할 평일 수업 시작 시간을 입력해 주세요.")
	private LocalTime weekdayStartTime;

	@Schema(description = "자동으로 생성할 평일 수업 종료 시간", example = "22:00:00", type = "string")
	@NotNull(message = "자동으로 생성할 평일 수업 종료 시간을 입력해 주세요.")
	private LocalTime weekdayEndTime;

	@Schema(description = "자동으로 생성할 주말 수업 시작 시간", example = "12:00:00", type = "string")
	@NotNull(message = "자동으로 생성할 주말 수업 시작 시간을 입력해 주세요.")
	private LocalTime weekendStartTime;

	@Schema(description = "자동으로 생성할 주말 수업 종료 시간", example = "20:00:00", type = "string")
	@NotNull(message = "자동으로 생성할 주말 수업 종료 시간을 입력해 주세요.")
	private LocalTime weekendEndTime;

	@Schema(description = "세션당 수업 시간", example = "50")
	@NotNull(message = "세션당 수업 시간을 입력해 주세요.")
	private int lessonTime;

	@Schema(description = "세션간 휴식 시간", example = "10")
	@NotNull(message = "세션간 휴식 시간을 입력해 주세요.")
	private int breakTime;
}

package com.tobe.healthy.schedule.domain.dto.in;

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
public class AutoCreateScheduleCommandRequest {
	@NotNull(message = "트레이너 아이디를 입력해 주세요.")
	private Long trainer;

	@NotNull(message = "등록할 수업 시작 일자를 입력해 주세요.")
	private LocalDate startDt;

	@NotNull(message = "등록할 수업 종료 일자를 입력해 주세요.")
	private LocalDate endDt;

	@NotNull(message = "등록할 평일 수업 시작 시간을 입력해 주세요.")
	private LocalTime weekdayStartTime;

	@NotNull(message = "등록할 평일 수업 종료 시간을 입력해 주세요.")
	private LocalTime weekdayEndTime;

	@NotNull(message = "등록할 주말 수업 시작 시간을 입력해 주세요.")
	private LocalTime weekendStartTime;

	@NotNull(message = "등록할 주말 수업 종료 시간을 입력해 주세요.")
	private LocalTime weekendEndTime;

	@NotNull(message = "세션당 수업 시간을 입력해 주세요.")
	private int lessonTime;

	@NotNull(message = "세션간 휴식 시간을 입력해 주세요.")
	private int breakTime;
}

package com.tobe.healthy.schedule.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "일정 조회 조건 DTO")
public class ScheduleSearchCond {
	@Schema(description = "조회할 수업 일자", example = "202404")
	private String lessonDt;

	@Schema(description = "조회할 수업 시작 일자", example = "2024-04-01")
	private LocalDate lessonStartDt;

	@Schema(description = "조회할 수업 종료 일자", example = "2024-04-30")
	private LocalDate lessonEndDt;
}

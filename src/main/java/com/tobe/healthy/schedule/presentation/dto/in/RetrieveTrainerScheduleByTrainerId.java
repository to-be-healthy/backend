package com.tobe.healthy.schedule.presentation.dto.in;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.error.ErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RetrieveTrainerScheduleByTrainerId {

	@Schema(description = "조회할 수업 시작 일자", example = "2024-04-01")
	private LocalDate lessonStartDt;

	@Schema(description = "조회할 수업 종료 일자", example = "2024-04-30")
	private LocalDate lessonEndDt;

	@Builder
	public RetrieveTrainerScheduleByTrainerId(LocalDate lessonStartDt, LocalDate lessonEndDt) {
		this.lessonStartDt = lessonStartDt;
		this.lessonEndDt = lessonEndDt;
		validateIfReady();
	}

	public void setLessonStartDt(LocalDate lessonStartDt) {
		this.lessonStartDt = lessonStartDt;
		validateIfReady();
	}

	public void setLessonEndDt(LocalDate lessonEndDt) {
		this.lessonEndDt = lessonEndDt;
		validateIfReady();
	}

	public void validate() {
		if (ChronoUnit.DAYS.between(lessonStartDt, lessonEndDt) > 31) {
			throw new CustomException(ErrorCode.SEARCH_LESS_THAN_31_DAYS);
		}
	}

	private void validateIfReady() {
		if (lessonStartDt != null && lessonEndDt != null) {
			validate();
		}
	}
}

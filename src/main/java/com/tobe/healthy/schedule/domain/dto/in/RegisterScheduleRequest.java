package com.tobe.healthy.schedule.domain.dto.in;

import com.tobe.healthy.schedule.domain.entity.LessonTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "일정 등록 DTO")
public class RegisterScheduleRequest {

	@Schema(description = "시작 수업 일자", example = "2024-04-01")
	@NotNull(message = "시작 수업 일자를 입력해 주세요.")
	private LocalDate startDt;

	@Schema(description = "종료 수업 일자", example = "2024-04-30")
	@NotNull(message = "종료 수업 일자를 입력해 주세요.")
	private LocalDate endDt;

	@Schema(description = "시작 수업 시간", example = "10:00:00", type = "string")
	@NotNull(message = "시작 수업 시간을 입력해 주세요.")
	private LocalTime startTime;

	@Schema(description = "종료 수업 시간", example = "22:00:00", type = "string")
	@NotNull(message = "종료 수업 시간을 입력해 주세요.")
	private LocalTime endTime;

	@Schema(description = "시작 점심시간", example = "12:00:00", type = "string")
	@NotNull(message = "시작 점심시간을 입력해 주세요.")
	private LocalTime lunchStartTime;

	@Schema(description = "종료 점심시간", example = "13:00:00", type = "string")
	@NotNull(message = "종료 점심시간을 입력해 주세요.")
	private LocalTime lunchEndTime;

	@Schema(description = "휴무일", example = "2024-04-05", type = "string")
	private List<LocalDate> closedDt;

	@Schema(description = "세션당 수업 시간", example = "30 | 60 | 90 | 120")
	@NotNull(message = "세션당 수업 시간을 입력해 주세요.")
	private LessonTime sessionTime;
}

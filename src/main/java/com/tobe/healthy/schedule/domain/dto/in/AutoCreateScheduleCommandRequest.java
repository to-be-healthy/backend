package com.tobe.healthy.schedule.domain.dto.in;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoCreateScheduleCommandRequest {
	@NotEmpty(message = "트레이너 아이디를 입력해 주세요.")
	private Long trainer;

	@NotEmpty(message = "수업 시작 시간을 입력해 주세요.")
	private LocalDateTime startDt;

	@NotEmpty(message = "수업 종료 시간을 입력해 주세요.")
	private LocalDateTime endDt;

	@NotEmpty(message = "세션당 수업 시간을 입력해 주세요.")
	private int lessonTime;

	@NotEmpty(message = "세션간 휴식 시간을 입력해 주세요.")
	private int breakTime;
}

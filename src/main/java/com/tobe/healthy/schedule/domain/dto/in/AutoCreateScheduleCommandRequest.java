package com.tobe.healthy.schedule.domain.dto.in;

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
	private Long trainer;
	private LocalDateTime startDt;
	private LocalDateTime endDt;
	private int lessonTime;
	private int breakTime;
}

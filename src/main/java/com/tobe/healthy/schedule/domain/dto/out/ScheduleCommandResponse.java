package com.tobe.healthy.schedule.domain.dto.out;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleCommandResponse {
	private int round;
	private LocalDateTime startDt;
	private LocalDateTime endDt;
}

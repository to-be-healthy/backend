package com.tobe.healthy.schedule.domain.dto.out;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleInfo {
	private LocalTime startTime;
	private LocalTime endTime;
	private int round;
}

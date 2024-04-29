package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ScheduleInfo {
	private LocalTime startTime;
	private LocalTime endTime;
	private int round;
	private ReservationStatus reservationStatus;
}

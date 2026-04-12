package com.tobe.healthy.schedule.presentation.dto.out;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tobe.healthy.schedule.domain.Schedule;

public record MyReservation(
	Long scheduleId,
	LocalDate lessonDt,
	LocalTime lessonStartTime,
	LocalTime lessonEndTime,
	String trainerName,
	String reservationStatus
) {
	public static MyReservation from(Schedule schedule) {
		return new MyReservation(
			schedule.getId(),
			schedule.getLessonDt(),
			schedule.getLessonStartTime(),
			schedule.getLessonEndTime(),
			schedule.getTrainer().getName() + " 트레이너",
			schedule.getReservationStatus().name()
		);
	}
}

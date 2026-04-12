package com.tobe.healthy.schedule.presentation.dto.out;

import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.schedule.domain.ReservationStatus;
import com.tobe.healthy.schedule.domain.Schedule;

public record CommandScheduleStatusResult(
	Long scheduleId,
	Long studentId,
	String studentName,
	Long trainerId,
	String lessonDt,
	String lessonTime,
	ReservationStatus reservationStatus
) {
	public static CommandScheduleStatusResult from(Schedule schedule) {
		return new CommandScheduleStatusResult(
			schedule.getId(),
			schedule.getApplicant() != null ? schedule.getApplicant().getId() : null,
			schedule.getApplicant() != null ? schedule.getApplicant().getName() : null,
			schedule.getTrainer() != null ? schedule.getTrainer().getId() : null,
			LessonTimeFormatter.formatLessonDt(schedule.getLessonDt()),
			LessonTimeFormatter.formatLessonTime(schedule.getLessonStartTime(), schedule.getLessonEndTime()),
			schedule.getReservationStatus()
		);
	}
}

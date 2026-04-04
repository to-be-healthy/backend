package com.tobe.healthy.schedule.presentation.dto.out;

import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.schedule.domain.ReservationStatus;
import com.tobe.healthy.schedule.domain.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandScheduleStatusResult {

	private Long scheduleId;
	private Long studentId;
	private String studentName;
	private Long trainerId;
	private String lessonDt;
	private String lessonTime;
	private ReservationStatus reservationStatus;

	public static CommandScheduleStatusResult from(Schedule schedule) {
		return CommandScheduleStatusResult.builder()
			.scheduleId(schedule.getId())
			.studentId(schedule.getApplicant() != null ? schedule.getApplicant().getId() : null)
			.studentName(schedule.getApplicant() != null ? schedule.getApplicant().getName() : null)
			.trainerId(schedule.getTrainer() != null ? schedule.getTrainer().getId() : null)
			.lessonDt(LessonTimeFormatter.formatLessonDt(schedule.getLessonDt()))
			.lessonTime(
				LessonTimeFormatter.formatLessonTime(schedule.getLessonStartTime(), schedule.getLessonEndTime()))
			.reservationStatus(schedule.getReservationStatus())
			.build();
	}
}

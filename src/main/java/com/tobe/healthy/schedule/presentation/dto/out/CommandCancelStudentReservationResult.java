package com.tobe.healthy.schedule.presentation.dto.out;

import java.time.LocalTime;

import com.tobe.healthy.schedule.domain.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandCancelStudentReservationResult {

	private Long scheduleId;
	private LocalTime lessonStartTime;
	private LocalTime lessonEndTime;
	private Long trainerId;
	private String trainerName;
	private Long studentId;
	private String studentName;
	private Long waitingStudentId;

	public static CommandCancelStudentReservationResult from(
		Schedule schedule,
		Long applicantId,
		String applicantName
	) {
		return CommandCancelStudentReservationResult.builder()
			.scheduleId(schedule.getId())
			.lessonStartTime(schedule.getLessonStartTime())
			.lessonEndTime(schedule.getLessonEndTime())
			.trainerId(schedule.getTrainer().getId())
			.trainerName(schedule.getTrainer().getName() + " 트레이너")
			.studentId(applicantId)
			.studentName(applicantName)
			.waitingStudentId(
				schedule.getScheduleWaiting() != null && !schedule.getScheduleWaiting().isEmpty()
					? schedule.getScheduleWaiting().get(0).getId()
					: null
			)
			.build();
	}
}

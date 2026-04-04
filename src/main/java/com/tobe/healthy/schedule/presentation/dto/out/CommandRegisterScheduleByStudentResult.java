package com.tobe.healthy.schedule.presentation.dto.out;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.schedule.domain.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRegisterScheduleByStudentResult {

	private Long scheduleId;
	private LocalDate lessonDt;
	private LocalTime lessonStartTime;
	private LocalTime lessonEndTime;
	private String studentName;
	private Long studentId;
	private Long trainerId;

	public static CommandRegisterScheduleByStudentResult from(Schedule schedule, Member student) {
		return CommandRegisterScheduleByStudentResult.builder()
			.scheduleId(schedule.getId())
			.lessonDt(schedule.getLessonDt())
			.lessonStartTime(schedule.getLessonStartTime())
			.lessonEndTime(schedule.getLessonEndTime())
			.studentId(student.getId())
			.studentName(student.getName())
			.trainerId(schedule.getTrainer().getId())
			.build();
	}
}

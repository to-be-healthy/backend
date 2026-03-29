package com.tobe.healthy.schedule.presentation.dto.out;

import java.time.DayOfWeek;
import java.util.List;

import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.schedule.presentation.dto.in.CommandRegisterDefaultLessonTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRegisterDefaultLessonTimeResult {

	private String lessonStartTime;
	private String lessonEndTime;
	private String lunchStartTime;
	private String lunchEndTime;
	private List<DayOfWeek> closedDays;
	private int lessonTime;

	public static CommandRegisterDefaultLessonTimeResult from(CommandRegisterDefaultLessonTime request) {
		return CommandRegisterDefaultLessonTimeResult.builder()
			.lessonStartTime(LessonTimeFormatter.formatLessonTime(request.getLessonStartTime()))
			.lessonEndTime(LessonTimeFormatter.formatLessonTime(request.getLessonEndTime()))
			.lunchStartTime(LessonTimeFormatter.formatLessonTime(request.getLunchStartTime()))
			.lunchEndTime(LessonTimeFormatter.formatLessonTime(request.getLunchEndTime()))
			.closedDays(request.getClosedDays())
			.lessonTime(request.getLessonTime())
			.build();
	}
}

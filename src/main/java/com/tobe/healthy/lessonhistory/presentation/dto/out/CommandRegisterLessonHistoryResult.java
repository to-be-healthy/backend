package com.tobe.healthy.lessonhistory.presentation.dto.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tobe.healthy.lessonhistory.domain.LessonHistory;
import com.tobe.healthy.lessonhistory.domain.LessonHistoryFiles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRegisterLessonHistoryResult {

	private Long lessonHistoryId;
	private String title;
	private String content;
	private Long scheduleId;
	private LocalDate lessonDt;
	private LocalTime lessonStartTime;
	private LocalTime lessonEndTime;
	private Long trainerId;
	private String trainerName;
	private Long studentId;
	private String studentName;
	@Builder.Default
	private List<CommandUploadFileResult> files = new ArrayList<>();

	public static CommandRegisterLessonHistoryResult from(LessonHistory lessonHistory, List<LessonHistoryFiles> files) {
		return CommandRegisterLessonHistoryResult.builder()
			.lessonHistoryId(lessonHistory.getId())
			.title(lessonHistory.getTitle())
			.content(lessonHistory.getContent())
			.scheduleId(lessonHistory.getSchedule() != null ? lessonHistory.getSchedule().getId() : null)
			.lessonDt(lessonHistory.getSchedule() != null ? lessonHistory.getSchedule().getLessonDt() : null)
			.lessonStartTime(
				lessonHistory.getSchedule() != null ? lessonHistory.getSchedule().getLessonStartTime() : null)
			.lessonEndTime(lessonHistory.getSchedule() != null ? lessonHistory.getSchedule().getLessonEndTime() : null)
			.trainerId(lessonHistory.getTrainer() != null ? lessonHistory.getTrainer().getId() : null)
			.trainerName(lessonHistory.getTrainer() != null ? lessonHistory.getTrainer().getName() : null)
			.studentId(lessonHistory.getStudent() != null ? lessonHistory.getStudent().getId() : null)
			.studentName(lessonHistory.getStudent() != null ? lessonHistory.getStudent().getName() : null)
			.files(files.stream().map(CommandUploadFileResult::from).collect(Collectors.toList()))
			.build();
	}
}

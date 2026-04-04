package com.tobe.healthy.lessonhistory.presentation.dto.out;

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
public class CommandUpdateLessonHistoryResult {

	private Long lessonHistoryId;
	private String title;
	private String content;
	@Builder.Default
	private List<CommandUploadFileResult> files = new ArrayList<>();

	public static CommandUpdateLessonHistoryResult from(LessonHistory lessonHistory, List<LessonHistoryFiles> files) {
		return CommandUpdateLessonHistoryResult.builder()
			.lessonHistoryId(lessonHistory.getId())
			.title(lessonHistory.getTitle())
			.content(lessonHistory.getContent())
			.files(files.stream().map(CommandUploadFileResult::from).collect(Collectors.toList()))
			.build();
	}
}

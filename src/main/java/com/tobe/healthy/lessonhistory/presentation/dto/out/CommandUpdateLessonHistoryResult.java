package com.tobe.healthy.lessonhistory.presentation.dto.out;

import java.util.List;
import java.util.stream.Collectors;

import com.tobe.healthy.lessonhistory.domain.LessonHistory;
import com.tobe.healthy.lessonhistory.domain.LessonHistoryFiles;

public record CommandUpdateLessonHistoryResult(
	Long lessonHistoryId,
	String title,
	String content,
	List<CommandUploadFileResult> files
) {
	public static CommandUpdateLessonHistoryResult from(LessonHistory lessonHistory, List<LessonHistoryFiles> files) {
		return new CommandUpdateLessonHistoryResult(
			lessonHistory.getId(),
			lessonHistory.getTitle(),
			lessonHistory.getContent(),
			files.stream().map(CommandUploadFileResult::from).collect(Collectors.toList())
		);
	}
}

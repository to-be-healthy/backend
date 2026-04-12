package com.tobe.healthy.lessonhistory.presentation.dto.out;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.tobe.healthy.lessonhistory.domain.LessonHistoryComment;

public record CommandUpdateCommentResult(
	Long lessonHistoryId,
	Long lessonHistoryCommentId,
	String content,
	List<CommandUploadFileResult> files
) {
	public static CommandUpdateCommentResult from(LessonHistoryComment lessonHistoryComment) {
		return new CommandUpdateCommentResult(
			lessonHistoryComment.getLessonHistory() != null ? lessonHistoryComment.getLessonHistory().getId() : null,
			lessonHistoryComment.getId(),
			lessonHistoryComment.getContent(),
			lessonHistoryComment.getFiles().stream()
				.map(CommandUploadFileResult::from)
				.sorted(Comparator.comparingInt(CommandUploadFileResult::fileOrder))
				.collect(Collectors.toList())
		);
	}
}

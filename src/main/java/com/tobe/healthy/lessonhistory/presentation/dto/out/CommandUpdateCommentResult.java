package com.tobe.healthy.lessonhistory.presentation.dto.out;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.tobe.healthy.lessonhistory.domain.LessonHistoryComment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandUpdateCommentResult {

	private Long lessonHistoryId;
	private Long lessonHistoryCommentId;
	private String content;
	@Builder.Default
	private List<CommandUploadFileResult> files = new ArrayList<>();

	public static CommandUpdateCommentResult from(LessonHistoryComment lessonHistoryComment) {
		return CommandUpdateCommentResult.builder()
			.lessonHistoryId(
				lessonHistoryComment.getLessonHistory() != null ? lessonHistoryComment.getLessonHistory().getId() :
					null)
			.lessonHistoryCommentId(lessonHistoryComment.getId())
			.content(lessonHistoryComment.getContent())
			.files(lessonHistoryComment.getFiles().stream()
				.map(CommandUploadFileResult::from)
				.sorted(Comparator.comparingInt(CommandUploadFileResult::getFileOrder))
				.collect(Collectors.toList()))
			.build();
	}
}

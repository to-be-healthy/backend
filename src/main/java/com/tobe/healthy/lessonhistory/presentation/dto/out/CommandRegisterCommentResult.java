package com.tobe.healthy.lessonhistory.presentation.dto.out;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryComment;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRegisterCommentResult {

	private Long lessonHistoryId;
	private Long lessonHistoryCommentId;
	private Long writerId;
	private String writerName;
	private String content;
	@Builder.Default
	private List<CommandUploadFileResult> files = new ArrayList<>();

	public static CommandRegisterCommentResult from(LessonHistoryComment lessonHistoryComment,
		List<LessonHistoryFiles> files) {
		return CommandRegisterCommentResult.builder()
			.lessonHistoryId(
				lessonHistoryComment.getLessonHistory() != null ? lessonHistoryComment.getLessonHistory().getId() :
					null)
			.lessonHistoryCommentId(lessonHistoryComment.getId())
			.writerId(lessonHistoryComment.getWriter() != null ? lessonHistoryComment.getWriter().getId() : null)
			.writerName(lessonHistoryComment.getWriter() != null ? lessonHistoryComment.getWriter().getName() : null)
			.content(lessonHistoryComment.getContent())
			.files(files.stream().map(CommandUploadFileResult::from).collect(Collectors.toList()))
			.build();
	}
}

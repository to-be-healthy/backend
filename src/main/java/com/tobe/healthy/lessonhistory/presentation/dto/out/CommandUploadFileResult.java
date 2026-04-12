package com.tobe.healthy.lessonhistory.presentation.dto.out;

import com.tobe.healthy.lessonhistory.domain.LessonHistoryFiles;

public record CommandUploadFileResult(
	String fileUrl,
	int fileOrder
) {
	public static CommandUploadFileResult from(LessonHistoryFiles lessonHistoryFiles) {
		return new CommandUploadFileResult(
			lessonHistoryFiles.getFileUrl(),
			lessonHistoryFiles.getFileOrder()
		);
	}
}

package com.tobe.healthy.lessonhistory.presentation.dto.out;

import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandUploadFileResult {

	private String fileUrl;
	private int fileOrder;

	public static CommandUploadFileResult from(LessonHistoryFiles lessonHistoryFiles) {
		return CommandUploadFileResult.builder()
			.fileUrl(lessonHistoryFiles.getFileUrl())
			.fileOrder(lessonHistoryFiles.getFileOrder())
			.build();
	}
}

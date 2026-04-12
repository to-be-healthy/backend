package com.tobe.healthy.lessonhistory.presentation.dto.in;

import com.tobe.healthy.lessonhistory.domain.WritingStatus;

public record UnwrittenLessonHistorySearchCond(
	String lessonDate,
	Long studentId,
	WritingStatus writingStatus
) {
}

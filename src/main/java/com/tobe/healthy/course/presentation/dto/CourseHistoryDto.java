package com.tobe.healthy.course.presentation.dto;

import java.time.LocalDateTime;

import com.tobe.healthy.course.domain.CourseHistory;
import com.tobe.healthy.course.domain.CourseHistoryType;
import com.tobe.healthy.point.domain.Calculation;

public record CourseHistoryDto(
	Long courseHistoryId,
	int cnt,
	Calculation calculation,
	CourseHistoryType type,
	LocalDateTime createdAt
) {

	public static CourseHistoryDto from(CourseHistory courseHistory) {
		return new CourseHistoryDto(
			courseHistory.getCourseHistoryId(),
			courseHistory.getCnt(),
			courseHistory.getCalculation(),
			courseHistory.getType(),
			courseHistory.getCreatedAt()
		);
	}
}

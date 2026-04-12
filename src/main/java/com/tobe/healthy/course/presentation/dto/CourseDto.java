package com.tobe.healthy.course.presentation.dto;

import java.time.LocalDateTime;

import com.tobe.healthy.course.domain.Course;

public record CourseDto(
	Long courseId,
	int totalLessonCnt,
	int remainLessonCnt,
	int completedLessonCnt,
	LocalDateTime createdAt
) {

	public static CourseDto from(Course course) {
		return new CourseDto(
			course.getCourseId(),
			course.getTotalLessonCnt(),
			course.getRemainLessonCnt(),
			course.getTotalLessonCnt() - course.getRemainLessonCnt(),
			course.getCreatedAt()
		);
	}

	public CourseDto withCompletedLessonCnt(int completedLessonCnt) {
		return new CourseDto(courseId, totalLessonCnt, remainLessonCnt, completedLessonCnt, createdAt);
	}
}

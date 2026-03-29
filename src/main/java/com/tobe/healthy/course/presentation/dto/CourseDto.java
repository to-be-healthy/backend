package com.tobe.healthy.course.presentation.dto;

import java.time.LocalDateTime;

import com.tobe.healthy.course.domain.entity.Course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

	private Long courseId;
	private int totalLessonCnt;
	private int remainLessonCnt;
	private int completedLessonCnt;
	private LocalDateTime createdAt;

	public static CourseDto from(Course course) {
		return CourseDto.builder()
			.courseId(course.getCourseId())
			.totalLessonCnt(course.getTotalLessonCnt())
			.remainLessonCnt(course.getRemainLessonCnt())
			.createdAt(course.getCreatedAt())
			.build();
	}
}

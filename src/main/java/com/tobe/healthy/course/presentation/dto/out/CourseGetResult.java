package com.tobe.healthy.course.presentation.dto.out;

import com.tobe.healthy.course.presentation.dto.CourseDto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CourseGetResult {

	private CourseDto course;
	private String gymName;

	public static CourseGetResult create(CourseDto courseDto, String gymName) {
		return CourseGetResult.builder()
			.course(courseDto)
			.gymName(gymName)
			.build();
	}
}

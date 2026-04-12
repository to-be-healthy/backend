package com.tobe.healthy.course.presentation.dto.out;

import com.tobe.healthy.course.presentation.dto.CourseDto;

public record CourseGetResult(
	CourseDto course,
	String gymName
) {

	public static CourseGetResult create(CourseDto courseDto, String gymName) {
		return new CourseGetResult(courseDto, gymName);
	}
}

package com.tobe.healthy.schedule.presentation.dto.out;

import java.util.List;

import org.springframework.util.ObjectUtils;

import com.tobe.healthy.course.presentation.dto.CourseDto;

public record FindMyScheduleWaitingResult(
	CourseDto course,
	List<MyScheduleWaiting> myScheduleWaitings
) {
	public static FindMyScheduleWaitingResult create(CourseDto course, List<MyScheduleWaiting> myScheduleWaitings) {
		return new FindMyScheduleWaitingResult(
			course,
			ObjectUtils.isEmpty(myScheduleWaitings) ? null : myScheduleWaitings
		);
	}
}

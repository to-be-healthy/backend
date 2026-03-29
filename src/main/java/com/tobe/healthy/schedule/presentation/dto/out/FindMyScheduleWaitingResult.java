package com.tobe.healthy.schedule.presentation.dto.out;

import java.util.List;

import org.springframework.util.ObjectUtils;

import com.tobe.healthy.course.presentation.dto.CourseDto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class FindMyScheduleWaitingResult {

	private CourseDto course;
	private List<MyScheduleWaiting> myScheduleWaitings;

	public static FindMyScheduleWaitingResult create(CourseDto course, List<MyScheduleWaiting> myScheduleWaitings) {
		return FindMyScheduleWaitingResult.builder()
			.course(course)
			.myScheduleWaitings(ObjectUtils.isEmpty(myScheduleWaitings) ? null : myScheduleWaitings)
			.build();
	}
}

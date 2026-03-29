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
public class MyReservationResponse {

	private final CourseDto course;
	private final List<MyReservation> reservations;

	public static MyReservationResponse create(CourseDto course, List<MyReservation> reservations) {
		return MyReservationResponse.builder()
			.course(course)
			.reservations(ObjectUtils.isEmpty(reservations) ? null : reservations)
			.build();
	}
}

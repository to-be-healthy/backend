package com.tobe.healthy.member.presentation.dto.out;

import com.tobe.healthy.course.presentation.dto.CourseDto;
import com.tobe.healthy.diet.presentation.dto.DietDto;
import com.tobe.healthy.gym.presentation.dto.out.GymDto;
import com.tobe.healthy.lessonhistory.presentation.dto.out.RetrieveLessonHistoryByDateCondResult;
import com.tobe.healthy.point.presentation.dto.out.PointDto;
import com.tobe.healthy.point.presentation.dto.out.RankDto;
import com.tobe.healthy.schedule.presentation.dto.out.MyReservation;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class StudentHomeResult {

	private CourseDto course;
	private PointDto point;
	private RankDto rank;
	private MyReservation myReservation;
	private RetrieveLessonHistoryByDateCondResult lessonHistory;
	private DietDto diet;
	private GymDto gym;
	private Boolean redDotStatus;

	public static StudentHomeResult create(CourseDto course, PointDto point, RankDto rank, MyReservation myReservation,
		RetrieveLessonHistoryByDateCondResult lessonHistory, DietDto diet, GymDto gym, Boolean redDotStatus) {
		return StudentHomeResult.builder()
			.course(course)
			.point(point)
			.rank(rank)
			.myReservation(myReservation)
			.lessonHistory(lessonHistory)
			.diet(diet)
			.gym(gym)
			.redDotStatus(redDotStatus)
			.build();
	}
}

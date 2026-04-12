package com.tobe.healthy.member.presentation.dto.out;

import com.tobe.healthy.course.presentation.dto.CourseDto;
import com.tobe.healthy.diet.presentation.dto.DietDto;
import com.tobe.healthy.gym.presentation.dto.out.GymDto;
import com.tobe.healthy.lessonhistory.presentation.dto.out.RetrieveLessonHistoryByDateCondResult;
import com.tobe.healthy.point.presentation.dto.out.PointDto;
import com.tobe.healthy.point.presentation.dto.out.RankDto;
import com.tobe.healthy.schedule.presentation.dto.out.MyReservation;

public record StudentHomeResult(
	CourseDto course,
	PointDto point,
	RankDto rank,
	MyReservation myReservation,
	RetrieveLessonHistoryByDateCondResult lessonHistory,
	DietDto diet,
	GymDto gym,
	Boolean redDotStatus
) {

	public static StudentHomeResult create(CourseDto course, PointDto point, RankDto rank, MyReservation myReservation,
		RetrieveLessonHistoryByDateCondResult lessonHistory, DietDto diet, GymDto gym, Boolean redDotStatus) {
		return new StudentHomeResult(course, point, rank, myReservation, lessonHistory, diet, gym, redDotStatus);
	}
}

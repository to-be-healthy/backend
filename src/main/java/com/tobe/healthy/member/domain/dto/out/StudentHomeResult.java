package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.diet.domain.dto.DietDto;
import com.tobe.healthy.lesson_history.domain.dto.out.LessonHistoryResponse;
import com.tobe.healthy.point.domain.dto.out.PointDto;
import com.tobe.healthy.point.domain.dto.out.RankDto;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentHomeResult {

    private CourseDto course;
    private PointDto point;
    private RankDto rank;
    private MyReservation myReservation;
    private LessonHistoryResponse lessonHistory;
    private DietDto diet;

    public static StudentHomeResult create(CourseDto course, PointDto point, RankDto rank, MyReservation myReservation, LessonHistoryResponse lessonHistory, DietDto diet) {
        return StudentHomeResult.builder()
                .course(course)
                .point(point)
                .rank(rank)
                .myReservation(myReservation)
                .lessonHistory(lessonHistory)
                .diet(diet)
                .build();
    }
}

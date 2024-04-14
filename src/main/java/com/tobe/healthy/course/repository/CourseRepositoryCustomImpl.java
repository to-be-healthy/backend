package com.tobe.healthy.course.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tobe.healthy.course.domain.entity.QCourse.course;
import static com.tobe.healthy.workout.domain.entity.QWorkoutHistory.workoutHistory;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryCustomImpl implements CourseRepositoryCustom{

    private final JPAQueryFactory queryFactory;



}

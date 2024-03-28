package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.workout.domain.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.file.domain.entity.QWorkoutHistoryFile.workoutHistoryFile;
import static com.tobe.healthy.workout.domain.entity.QExercise.exercise;
import static com.tobe.healthy.workout.domain.entity.QInstructions.instructions1;
import static com.tobe.healthy.workout.domain.entity.QWorkoutHistory.workoutHistory;
import static com.tobe.healthy.workout.domain.entity.QWorkoutHistoryComment.workoutHistoryComment;


@Repository
@RequiredArgsConstructor
public class ExerciseRepositoryCustomImpl implements ExerciseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Exercise> getExercise(ExerciseCategory category, PrimaryMuscle primaryMuscle, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(exercise.count())
                .from(exercise)
                .where(categoryEq(category), primaryMuscleEq(primaryMuscle))
                .fetchOne();
        List<Exercise> exercises = queryFactory
                .select(exercise)
                .from(exercise)
                .where(categoryEq(category), primaryMuscleEq(primaryMuscle))
                .groupBy(exercise.exerciseId)
                .orderBy(exercise.exerciseId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(exercises, pageable, ()-> totalCnt );
    }

    @Override
    public List<Instructions> getInstructions(List<Long> ids) {
        return queryFactory
                .select(instructions1)
                .from(instructions1)
                .where(instructions1.exerciseId.in(ids))
                .orderBy(instructions1.exerciseId.asc())
                .fetch();
    }

    private BooleanExpression categoryEq(ExerciseCategory category) {
        if (!ObjectUtils.isEmpty(category)){
            return exercise.category.eq(category);
        }
        return null;
    }

    private BooleanExpression primaryMuscleEq(PrimaryMuscle primaryMuscle) {
        if (!ObjectUtils.isEmpty(primaryMuscle)){
            return exercise.primaryMuscle.eq(primaryMuscle);
        }
        return null;
    }

}

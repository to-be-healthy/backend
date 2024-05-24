package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.workout.domain.entity.Exercise;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.workout.domain.entity.QExercise.exercise;


@Repository
@RequiredArgsConstructor
public class ExerciseRepositoryCustomImpl implements ExerciseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Exercise> getExercise(ExerciseCategory exerciseCategory, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(exercise.count())
                .from(exercise)
                .where(exerciseCategoryEq(exerciseCategory))
                .fetchOne();
        List<Exercise> exercises = queryFactory
                .select(exercise)
                .from(exercise)
                .where(exerciseCategoryEq(exerciseCategory))
                .groupBy(exercise.exerciseId)
                .orderBy(exercise.exerciseId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(exercises, pageable, ()-> totalCnt );
    }

    private BooleanExpression exerciseCategoryEq(ExerciseCategory category) {
        if (!ObjectUtils.isEmpty(category)){
            return exercise.category.eq(category);
        }
        return null;
    }

}

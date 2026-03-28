package com.tobe.healthy.workout.repository.workoutHistory;

import static com.tobe.healthy.workout.domain.entity.workoutHistory.QCompletedExercise.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.workout.domain.entity.workoutHistory.CompletedExercise;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompletedExerciseRepositoryCustomImpl implements CompletedExerciseRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<CompletedExercise> getCompletedExercise(List<Long> ids) {
		return queryFactory.select(completedExercise)
			.from(completedExercise)
			.where(completedExercise.workoutHistory.workoutHistoryId.in(ids))
			.orderBy(completedExercise.exerciseId.asc())
			.fetch();
	}

}

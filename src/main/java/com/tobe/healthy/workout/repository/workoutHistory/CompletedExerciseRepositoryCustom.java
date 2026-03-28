package com.tobe.healthy.workout.repository.workoutHistory;

import java.util.List;

import com.tobe.healthy.workout.domain.entity.workoutHistory.CompletedExercise;

public interface CompletedExerciseRepositoryCustom {

	List<CompletedExercise> getCompletedExercise(List<Long> ids);
}

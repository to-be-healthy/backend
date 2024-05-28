package com.tobe.healthy.workout.repository.workoutHistory;

import com.tobe.healthy.workout.domain.entity.workoutHistory.CompletedExercise;

import java.util.List;

public interface CompletedExerciseRepositoryCustom {

    List<CompletedExercise> getCompletedExercise(List<Long> ids);
}

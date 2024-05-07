package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.CompletedExercise;

import java.util.List;

public interface CompletedExerciseRepositoryCustom {

    List<CompletedExercise> getCompletedExercise(List<Long> ids);
}

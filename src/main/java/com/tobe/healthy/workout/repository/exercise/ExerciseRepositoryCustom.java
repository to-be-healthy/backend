package com.tobe.healthy.workout.repository.exercise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;

public interface ExerciseRepositoryCustom {

	Page<Exercise> getExercise(Long memberId, ExerciseCategory exerciseCategory, Pageable pageable, String searchValue);

}

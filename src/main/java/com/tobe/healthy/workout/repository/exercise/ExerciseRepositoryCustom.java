package com.tobe.healthy.workout.repository.exercise;

import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExerciseRepositoryCustom {

    Page<Exercise> getExercise(ExerciseCategory exerciseCategory, Pageable pageable);

}

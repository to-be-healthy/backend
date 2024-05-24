package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.Exercise;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExerciseRepositoryCustom {

    Page<Exercise> getExercise(ExerciseCategory exerciseCategory, Pageable pageable);

}

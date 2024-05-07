package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.Exercise;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.Instructions;
import com.tobe.healthy.workout.domain.entity.PrimaryMuscle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExerciseRepositoryCustom {

    Page<Exercise> getExercise(ExerciseCategory category, PrimaryMuscle primaryMuscle, Pageable pageable);
    List<Instructions> getInstructions(List<Long> ids);

}

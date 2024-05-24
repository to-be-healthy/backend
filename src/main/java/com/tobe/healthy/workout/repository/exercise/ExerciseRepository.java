package com.tobe.healthy.workout.repository.exercise;

import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long>, ExerciseRepositoryCustom {
}

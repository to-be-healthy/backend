package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long>, ExerciseRepositoryCustom {
}

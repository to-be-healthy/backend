package com.tobe.healthy.workout.repository.workoutHistory;

import com.tobe.healthy.workout.domain.entity.workoutHistory.CompletedExercise;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompletedExerciseRepository extends JpaRepository<CompletedExercise, Long>, CompletedExerciseRepositoryCustom {

}

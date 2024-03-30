package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.CompletedExercise;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompletedExerciseRepository extends JpaRepository<CompletedExercise, Long>, CompletedExerciseRepositoryCustom {

}

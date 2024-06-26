package com.tobe.healthy.workout.repository.exercise;

import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long>, ExerciseRepositoryCustom {
    Optional<Exercise> findByExerciseIdAndMemberId(Long exerciseId, Long memberId);
    Optional<Exercise> findByMemberIdAndNames(Long memberId, String names);

}

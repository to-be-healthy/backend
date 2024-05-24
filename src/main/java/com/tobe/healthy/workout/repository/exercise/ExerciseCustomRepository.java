package com.tobe.healthy.workout.repository.exercise;

import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseCustomRepository extends JpaRepository<ExerciseCustom, Long> {
    Optional<ExerciseCustom> findByExerciseCustomIdAndMemberId(Long exerciseCustomId, Long memberId);
    List<ExerciseCustom> findByMemberIdAndCategory(Long memberId, ExerciseCategory category);

}

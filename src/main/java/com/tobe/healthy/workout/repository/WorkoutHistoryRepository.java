package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutHistoryRepository extends JpaRepository<WorkoutHistory, Long>, WorkoutHistoryRepositoryCustom {

    Optional<WorkoutHistory> findByWorkoutHistoryIdAndMemberId(Long workoutHistoryId, Long memberId);

}

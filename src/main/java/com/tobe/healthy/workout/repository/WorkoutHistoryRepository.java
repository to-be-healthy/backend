package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutHistoryRepository extends JpaRepository<WorkoutHistory, Long>, WorkoutHistoryRepositoryCustom {

    Optional<WorkoutHistory> findByWorkoutHistoryIdAndMemberIdAndDelYnFalse(Long workoutHistoryId, Long memberId);
    Optional<WorkoutHistory> findByWorkoutHistoryIdAndDelYnFalse(Long workoutHistoryId);

}

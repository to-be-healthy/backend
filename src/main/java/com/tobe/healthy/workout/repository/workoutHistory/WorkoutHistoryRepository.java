package com.tobe.healthy.workout.repository.workoutHistory;

import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutHistoryRepository extends JpaRepository<WorkoutHistory, Long>, WorkoutHistoryRepositoryCustom {

    Optional<WorkoutHistory> findByWorkoutHistoryIdAndMemberIdAndDelYnFalse(Long workoutHistoryId, Long memberId);

    @EntityGraph(attributePaths = {"member"})
    Optional<WorkoutHistory> findByWorkoutHistoryIdAndDelYnFalse(Long workoutHistoryId);

}

package com.tobe.healthy.workout.repository.workoutHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryComment;

public interface WorkoutHistoryCommentRepositoryCustom {

	Page<WorkoutHistoryComment> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable);
}

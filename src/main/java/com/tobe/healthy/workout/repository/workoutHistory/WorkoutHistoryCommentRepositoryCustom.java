package com.tobe.healthy.workout.repository.workoutHistory;


import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkoutHistoryCommentRepositoryCustom {

    Page<WorkoutHistoryComment> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable);
}

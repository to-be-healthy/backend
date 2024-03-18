package com.tobe.healthy.workout.repository;


import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkoutHistoryCommentRepositoryCustom {

    Page<WorkoutHistoryComment> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable);
}

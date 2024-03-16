package com.tobe.healthy.workout.repository;


import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkoutHistoryCommentRepositoryCustom {

    Page<WorkoutHistoryCommentDto> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable);
}

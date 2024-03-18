package com.tobe.healthy.workout.repository;

import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutHistoryRepositoryCustom {

    Page<WorkoutHistory> getWorkoutHistory(@Param("memberId") Long memberId, Pageable pageable);
    Page<WorkoutHistory> getWorkoutHistoryByTrainer(@Param("trainerId") Long trainerId, Pageable pageable);
    List<WorkoutHistoryFile> getWorkoutHistoryFile(@Param("ids") List<Long> ids);
}

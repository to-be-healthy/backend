package com.tobe.healthy.workout.repository.workoutHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryFiles;

public interface WorkoutFileRepository extends JpaRepository<WorkoutHistoryFiles, Long> {

}

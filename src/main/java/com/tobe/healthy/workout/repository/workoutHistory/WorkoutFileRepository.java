package com.tobe.healthy.workout.repository.workoutHistory;

import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutFileRepository extends JpaRepository<WorkoutHistoryFiles, Long> {

}

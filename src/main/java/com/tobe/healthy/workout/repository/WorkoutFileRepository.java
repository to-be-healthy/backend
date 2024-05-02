package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.WorkoutHistoryFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutFileRepository extends JpaRepository<WorkoutHistoryFile, Long> {

}

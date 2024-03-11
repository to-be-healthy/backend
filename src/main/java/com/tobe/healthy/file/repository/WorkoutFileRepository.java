package com.tobe.healthy.file.repository;

import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutFileRepository extends JpaRepository<WorkoutHistoryFile, Long> {

}

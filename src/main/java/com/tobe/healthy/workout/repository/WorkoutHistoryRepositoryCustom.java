package com.tobe.healthy.workout.repository;

import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkoutHistoryRepositoryCustom {

    Page<WorkoutHistoryDto> getWorkoutHistory(Long memberId, Pageable pageable);
    List<WorkoutHistoryFileDto> getWorkoutHistoryFile(List<Long> ids);
}

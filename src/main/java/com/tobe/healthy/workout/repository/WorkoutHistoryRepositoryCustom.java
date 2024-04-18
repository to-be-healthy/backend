package com.tobe.healthy.workout.repository;

import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutHistoryRepositoryCustom {

    Page<WorkoutHistory> getWorkoutHistoryOfMonth(@Param("memberId") Long memberId, Pageable pageable, String searchDate);
    Page<WorkoutHistory> getWorkoutHistoryByTrainer(Member trainer, Pageable pageable);
    List<WorkoutHistoryFile> getWorkoutHistoryFile(@Param("ids") List<Long> ids);
}

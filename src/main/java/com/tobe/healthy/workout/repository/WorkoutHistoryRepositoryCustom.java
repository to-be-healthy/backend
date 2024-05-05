package com.tobe.healthy.workout.repository;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryFiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutHistoryRepositoryCustom {

    Page<WorkoutHistoryDto> getWorkoutHistoryOfMonth(Long loginMemberId, Long memberId, Pageable pageable, String searchDate);
    Page<WorkoutHistory> getWorkoutHistoryByGym(Long gymId, Pageable pageable, String searchDate);
    List<WorkoutHistoryFiles> getWorkoutHistoryFile(@Param("ids") List<Long> ids);

}

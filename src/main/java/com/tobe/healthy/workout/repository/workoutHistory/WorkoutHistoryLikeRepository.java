package com.tobe.healthy.workout.repository.workoutHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.workout.domain.WorkoutHistoryLike;
import com.tobe.healthy.workout.domain.WorkoutHistoryLikePK;

public interface WorkoutHistoryLikeRepository
	extends JpaRepository<WorkoutHistoryLike, WorkoutHistoryLikePK>, WorkoutHistoryLikeRepositoryCustom {

}

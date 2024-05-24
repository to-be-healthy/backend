package com.tobe.healthy.workout.repository.workoutHistory;

import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryLike;
import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryLikePK;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkoutHistoryLikeRepository extends JpaRepository<WorkoutHistoryLike, WorkoutHistoryLikePK>, WorkoutHistoryLikeRepositoryCustom {

}

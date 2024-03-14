package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.WorkoutHistoryLike;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryLikePK;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkoutHistoryLikeRepository extends JpaRepository<WorkoutHistoryLike, WorkoutHistoryLikePK>, WorkoutHistoryLikeRepositoryCustom {

}

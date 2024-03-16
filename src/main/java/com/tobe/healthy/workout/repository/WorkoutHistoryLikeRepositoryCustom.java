package com.tobe.healthy.workout.repository;


public interface WorkoutHistoryLikeRepositoryCustom {

    Long getLikeCnt(Long workoutHistoryId);
    void deleteLikeByWorkoutHistoryId(Long workoutHistoryId);

}

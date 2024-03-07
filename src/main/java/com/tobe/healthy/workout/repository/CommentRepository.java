package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<WorkoutHistoryComment, Long> {

}

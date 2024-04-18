package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkoutHistoryCommentRepository extends JpaRepository<WorkoutHistoryComment, Long>, WorkoutHistoryCommentRepositoryCustom {

    Optional<WorkoutHistoryComment> findByCommentIdAndMemberIdAndDelYnFalse(Long commentId, Long memberId);

    Long countByWorkoutHistoryAndParentCommentIdAndDelYnFalse(WorkoutHistory history, Long parentCommentId);

    Long countByWorkoutHistory(WorkoutHistory history);

    Optional<WorkoutHistoryComment> findByCommentIdAndDelYnFalse(@Param("commentId") Long parentCommentId);

}

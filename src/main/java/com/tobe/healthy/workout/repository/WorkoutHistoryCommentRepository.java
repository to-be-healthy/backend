package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutHistoryCommentRepository extends JpaRepository<WorkoutHistoryComment, Long>, WorkoutHistoryCommentRepositoryCustom {

    Optional<WorkoutHistoryComment> findByCommentIdAndMemberIdAndDelYnFalse(Long commentId, Long memberId);

}

package com.tobe.healthy.workout.repository;

import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<WorkoutHistoryComment, Long> {

    Optional<WorkoutHistoryComment> findByCommentIdAndMemberIdAndDelYnFalse(Long commentId, Long memberId);

}

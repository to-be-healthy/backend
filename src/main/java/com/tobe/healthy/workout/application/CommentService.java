package com.tobe.healthy.workout.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryCommentAddCommand;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import com.tobe.healthy.workout.repository.WorkoutHistoryCommentRepository;
import com.tobe.healthy.workout.repository.WorkoutHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tobe.healthy.config.error.ErrorCode.WORKOUT_HISTORY_COMMENT_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.WORKOUT_HISTORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final WorkoutHistoryCommentRepository commentRepository;

    @Transactional
    public WorkoutHistoryCommentDto addComment(Long workoutHistoryId, HistoryCommentAddCommand command, Member member) {
        WorkoutHistory history = workoutHistoryRepository.findById(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        WorkoutHistoryComment comment = WorkoutHistoryComment.create(history, member, command.getContent());
        commentRepository.save(comment);
        return WorkoutHistoryCommentDto.from(comment);
    }

    @Transactional
    public WorkoutHistoryCommentDto updateComment(Member member, Long workoutHistoryId, Long commentId, HistoryCommentAddCommand command) {
        WorkoutHistoryComment comment = commentRepository.findByCommentIdAndMemberIdAndDelYnFalse(commentId, member.getId())
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_COMMENT_NOT_FOUND));
        comment.updateContent(command.getContent());
        return WorkoutHistoryCommentDto.from(comment);
    }

    public List<WorkoutHistoryCommentDto> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable) {
        return commentRepository.getCommentsByWorkoutHistoryId(workoutHistoryId, pageable).stream().toList();
    }

    @Transactional
    public void deleteComment(Member member, Long workoutHistoryId, Long commentId) {
        WorkoutHistoryComment comment = commentRepository.findByCommentIdAndMemberIdAndDelYnFalse(commentId, member.getId())
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_COMMENT_NOT_FOUND));
        comment.deleteComment();
    }
}

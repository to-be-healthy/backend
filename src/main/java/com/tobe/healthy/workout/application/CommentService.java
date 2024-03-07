package com.tobe.healthy.workout.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryCommentAddCommand;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import com.tobe.healthy.workout.repository.CommentRepository;
import com.tobe.healthy.workout.repository.WorkoutHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tobe.healthy.config.error.ErrorCode.WORKOUT_HISTORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public WorkoutHistoryCommentDto addComments(Long workoutHistoryId, HistoryCommentAddCommand command, Member member) {
        WorkoutHistory history = workoutHistoryRepository.findByWorkoutHistoryIdAndMemberId(workoutHistoryId, member.getId())
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        WorkoutHistoryComment comment = WorkoutHistoryComment.create(history, member, command.getContent());
        commentRepository.save(comment);
        return WorkoutHistoryCommentDto.from(comment);
    }
}

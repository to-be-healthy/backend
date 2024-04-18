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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.WORKOUT_HISTORY_COMMENT_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.WORKOUT_HISTORY_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final WorkoutHistoryCommentRepository commentRepository;

    public void addComment(Long workoutHistoryId, HistoryCommentAddCommand command, Member member) {
        WorkoutHistory history = workoutHistoryRepository.findById(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        Long depth = 0L, orderNum = 0L;
        if(command.getParentCommentId() == null){ //댓글
            depth = 0L;
            orderNum = commentRepository.countByWorkoutHistoryAndDelYnFalse(history);
        }else{ //대댓글
            WorkoutHistoryComment parentComment = commentRepository.findByCommentIdAndDelYnFalse(command.getParentCommentId())
                    .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_COMMENT_NOT_FOUND));
            depth = parentComment.getDepth()+1;
            orderNum = parentComment.getOrderNum();
        }
        WorkoutHistoryComment comment = WorkoutHistoryComment.create(history, member, command, depth, orderNum);
        commentRepository.save(comment);
    }

    public WorkoutHistoryCommentDto updateComment(Member member, Long workoutHistoryId, Long commentId, HistoryCommentAddCommand command) {
        WorkoutHistoryComment comment = commentRepository.findByCommentIdAndMemberIdAndDelYnFalse(commentId, member.getId())
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_COMMENT_NOT_FOUND));
        comment.updateContent(command.getContent());
        return WorkoutHistoryCommentDto.from(comment);
    }

    public List<WorkoutHistoryCommentDto> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable) {
        List<WorkoutHistoryComment> comments = commentRepository.getCommentsByWorkoutHistoryId(workoutHistoryId, pageable).stream().toList();
        if(comments.isEmpty()) return null;

        List<WorkoutHistoryCommentDto> dtos = comments.stream()
                .map(c -> WorkoutHistoryCommentDto.create(c, c.getMember().getProfileId())).toList();
        Map<Boolean, List<WorkoutHistoryCommentDto>> dtos2 = dtos.stream()
                .collect(Collectors.partitioningBy(c -> c.getParentCommentId() == null));
        List<WorkoutHistoryCommentDto> parent = dtos2.get(true);
        List<WorkoutHistoryCommentDto> child = dtos2.get(false);

        Map<Long, List<WorkoutHistoryCommentDto>> childByGroupList = child.stream()
                .collect(Collectors.groupingBy(WorkoutHistoryCommentDto::getParentCommentId, Collectors.toList()));
        return parent.stream().peek(p -> p.setReply(childByGroupList.get(p.getCommentId()))).toList();
    }

    public void deleteComment(Member member, Long workoutHistoryId, Long commentId) {
        WorkoutHistoryComment comment = commentRepository.findByCommentIdAndMemberIdAndDelYnFalse(commentId, member.getId())
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_COMMENT_NOT_FOUND));
        comment.deleteComment();
    }
}

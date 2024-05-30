package com.tobe.healthy.workout.application;

import com.tobe.healthy.common.CustomPaging;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryCommentAddCommand;
import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistory;
import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryComment;
import com.tobe.healthy.workout.repository.workoutHistory.WorkoutHistoryCommentRepository;
import com.tobe.healthy.workout.repository.workoutHistory.WorkoutHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.COMMENT_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.WORKOUT_HISTORY_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkoutCommentService {

    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final WorkoutHistoryCommentRepository commentRepository;

    public void addComment(Long workoutHistoryId, HistoryCommentAddCommand command, Member member) {
        WorkoutHistory history = workoutHistoryRepository.findById(workoutHistoryId)
                .orElseThrow(() -> new CustomException(WORKOUT_HISTORY_NOT_FOUND));
        Long depth, orderNum;
        Long commentCnt = commentRepository.countByWorkoutHistory(history);
        if(command.getParentCommentId() == null){ //댓글
            depth = 0L;
            orderNum = commentCnt;
        }else{ //대댓글
            WorkoutHistoryComment parentComment = commentRepository.findByCommentIdAndDelYnFalse(command.getParentCommentId())
                    .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
            depth = parentComment.getDepth()+1;
            orderNum = parentComment.getOrderNum();
        }
        commentRepository.save(WorkoutHistoryComment.create(history, member, command, depth, orderNum));
        history.changeCommentCnt(++commentCnt);
    }

    public WorkoutHistoryCommentDto updateComment(Member member, Long workoutHistoryId, Long commentId, HistoryCommentAddCommand command) {
        WorkoutHistoryComment comment = commentRepository.findByCommentIdAndMemberIdAndDelYnFalse(commentId, member.getId())
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        comment.updateContent(command.getContent());
        return WorkoutHistoryCommentDto.from(comment);
    }

    public CustomPaging<WorkoutHistoryCommentDto> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable) {
        Page<WorkoutHistoryComment> pageDtos = commentRepository.getCommentsByWorkoutHistoryId(workoutHistoryId, pageable);
        List<WorkoutHistoryComment> comments = pageDtos.stream().toList();
        return new CustomPaging(settingReplyFormat(comments), pageDtos.getPageable().getPageNumber(),
                pageDtos.getPageable().getPageSize(), pageDtos.getTotalPages(), pageDtos.getTotalElements(), pageDtos.isLast());
    }

    private List<WorkoutHistoryCommentDto> settingReplyFormat(List<WorkoutHistoryComment> comments) {
        List<WorkoutHistoryCommentDto> dtos = comments.stream()
                .map(c -> WorkoutHistoryCommentDto.create(c, c.getMember().getMemberProfile())).toList();
        Map<Boolean, List<WorkoutHistoryCommentDto>> dtos2 = dtos.stream()
                .collect(Collectors.partitioningBy(c -> c.getParentId() == null));
        List<WorkoutHistoryCommentDto> parent = dtos2.get(true);
        List<WorkoutHistoryCommentDto> child = dtos2.get(false);

        Map<Long, List<WorkoutHistoryCommentDto>> childByGroupList = child.stream()
                .collect(Collectors.groupingBy(WorkoutHistoryCommentDto::getParentId, Collectors.toList()));
        return parent.stream().peek(p -> p.setReplies(childByGroupList.get(p.getId()))).toList();
    }

    public void deleteComment(Member member, Long workoutHistoryId, Long commentId) {
        WorkoutHistoryComment comment = commentRepository.findByCommentIdAndMemberIdAndDelYnFalse(commentId, member.getId())
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        comment.deleteComment();
    }
}

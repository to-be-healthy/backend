package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.member.domain.entity.MemberProfile;
import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutHistoryCommentDto {

    private Long id;
    private CommentMemberDto member;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentId;
    private Long orderNum;
    private boolean delYn;

    @Builder.Default
    private List<WorkoutHistoryCommentDto> replies = null;


    public static WorkoutHistoryCommentDto from(WorkoutHistoryComment comment) {
        return WorkoutHistoryCommentDto.builder()
                .id(comment.getCommentId())
                .member(CommentMemberDto.from(comment.getMember()))
                .content(comment.getDelYn() ? "삭제된 댓글입니다." : comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentId(comment.getParentCommentId())
                .orderNum(comment.getOrderNum())
                .delYn(comment.getDelYn())
                .build();
    }

    public static WorkoutHistoryCommentDto create(WorkoutHistoryComment comment, MemberProfile memberProfile) {
        return WorkoutHistoryCommentDto.builder()
                .id(comment.getCommentId())
                .member(CommentMemberDto.create(comment.getMember(), memberProfile))
                .content(comment.getDelYn() ? "삭제된 댓글입니다." : comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentId(comment.getParentCommentId())
                .orderNum(comment.getOrderNum())
                .delYn(comment.getDelYn())
                .build();
    }
}

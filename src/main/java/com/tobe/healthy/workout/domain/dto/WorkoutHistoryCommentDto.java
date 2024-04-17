package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutHistoryCommentDto {

    private Long commentId;
    private MemberDto member;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long parentCommentId;
    private Long depth;
    private Long orderNum;
    private boolean delYn;

    @Builder.Default
    private List<WorkoutHistoryCommentDto> reply = null;


    public static WorkoutHistoryCommentDto from(WorkoutHistoryComment comment) {
        return WorkoutHistoryCommentDto.builder()
                .commentId(comment.getCommentId())
                .member(MemberDto.from(comment.getMember()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentCommentId(comment.getParentCommentId())
                .depth(comment.getDepth())
                .orderNum(comment.getOrderNum())
                .delYn(comment.getDelYn())
                .build();
    }

    public static WorkoutHistoryCommentDto create(WorkoutHistoryComment comment, Profile profile) {
        return WorkoutHistoryCommentDto.builder()
                .commentId(comment.getCommentId())
                .member(MemberDto.create(comment.getMember(), profile))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentCommentId(comment.getParentCommentId())
                .depth(comment.getDepth())
                .orderNum(comment.getOrderNum())
                .delYn(comment.getDelYn())
                .build();
    }
}

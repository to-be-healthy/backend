package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutHistoryCommentDto {

    private Long commentId;
    private WorkoutHistoryDto workoutHistory;
    private MemberDto member;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String name;

    public static WorkoutHistoryCommentDto from(WorkoutHistoryComment comment) {
        return WorkoutHistoryCommentDto.builder()
                .commentId(comment.getCommentId())
                .workoutHistory(WorkoutHistoryDto.create(comment.getWorkoutHistory()))
                .member(MemberDto.from(comment.getMember()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

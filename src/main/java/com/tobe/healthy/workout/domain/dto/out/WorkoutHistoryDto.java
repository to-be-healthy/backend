package com.tobe.healthy.workout.domain.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberProfile;
import com.tobe.healthy.workout.domain.dto.CompletedExerciseDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutHistoryDto {

    private Long workoutHistoryId;
    private String content;
    private MemberDto member;
    private boolean liked;
    private Long likeCnt;
    private Long commentCnt;
    private boolean viewMySelf;
    private LocalDateTime createdAt;

    @Builder.Default
    @JsonIgnore
    private List<MultipartFile> multipartFiles = new ArrayList<>();

    @Builder.Default
    private List<WorkoutHistoryFileDto> files = new ArrayList<>();

    @Builder.Default
    private List<CompletedExerciseDto> completedExercises = new ArrayList<>();


    public static WorkoutHistoryDto create(HistoryAddCommand command, MemberDto memberDto) {
        WorkoutHistoryDtoBuilder builder = WorkoutHistoryDto.builder()
                .content(command.getContent())
                .member(memberDto);
                return builder.build();
    }

    public static WorkoutHistoryDto create(WorkoutHistory history, MemberProfile memberProfile) {
        return WorkoutHistoryDto.builder()
                .workoutHistoryId(history.getWorkoutHistoryId())
                .content(history.getContent())
                .member(MemberDto.create(history.getMember(), memberProfile))
                .likeCnt(history.getLikeCnt())
                .commentCnt(history.getCommentCnt())
                .viewMySelf(history.getViewMySelf())
                .createdAt(history.getCreatedAt())
                .build();
    }

    public static WorkoutHistoryDto from(WorkoutHistory history) {
        return WorkoutHistoryDto.builder()
                .workoutHistoryId(history.getWorkoutHistoryId())
                .content(history.getContent())
                .member(MemberDto.from(history.getMember()))
                .likeCnt(history.getLikeCnt())
                .commentCnt(history.getCommentCnt())
                .viewMySelf(history.getViewMySelf())
                .createdAt(history.getCreatedAt())
                .build();
    }

    @QueryProjection
    public WorkoutHistoryDto(Long workoutHistoryId, String content, Member member, boolean liked, Long likeCnt, Long commentCnt, boolean viewMySelf, LocalDateTime createdAt, MemberProfile profile) {
        this.workoutHistoryId = workoutHistoryId;
        this.content = content;
        this.member = MemberDto.create(member, profile);
        this.liked = liked;
        this.likeCnt = likeCnt;
        this.commentCnt = commentCnt;
        this.createdAt = createdAt;
        this.viewMySelf = viewMySelf;
    }

}

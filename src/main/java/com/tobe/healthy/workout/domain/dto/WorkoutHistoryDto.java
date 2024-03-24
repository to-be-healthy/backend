package com.tobe.healthy.workout.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.workout.domain.dto.in.HistoryAddCommand;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutHistoryDto {

    private Long workoutHistoryId;
    private String content;
    private Long trainerId;
    private MemberDto member;
    private Long likeCnt;

    @Builder.Default
    @JsonIgnore
    private List<MultipartFile> multipartFiles = new ArrayList<>();

    @Builder.Default
    private List<WorkoutHistoryFileDto> files = new ArrayList<>();

    @Builder.Default
    private List<CompletedExerciseDto> completedExercises = new ArrayList<>();


    public static WorkoutHistoryDto create(HistoryAddCommand command, MemberDto memberDto, Long trainerId) {
        return WorkoutHistoryDto.builder()
                .content(command.getContent())
                .member(memberDto)
                .multipartFiles(command.getFiles())
                .trainerId(trainerId)
                .build();
    }

    public static WorkoutHistoryDto create(WorkoutHistory history, List<WorkoutHistoryFileDto> files) {
        return WorkoutHistoryDto.builder()
                .workoutHistoryId(history.getWorkoutHistoryId())
                .content(history.getContent())
                .member(MemberDto.from(history.getMember()))
                .files(files)
                .trainerId(history.getTrainerId())
                .likeCnt(history.getLikeCnt())
                .build();
    }

    public static WorkoutHistoryDto from(WorkoutHistory history) {
        return WorkoutHistoryDto.builder()
                .workoutHistoryId(history.getWorkoutHistoryId())
                .content(history.getContent())
                .member(MemberDto.from(history.getMember()))
                .trainerId(history.getTrainerId())
                .likeCnt(history.getLikeCnt())
                .build();
    }

}

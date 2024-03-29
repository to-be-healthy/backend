package com.tobe.healthy.file.domain.dto;

import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutHistoryFileDto {

    private Long id;
    private Long workoutHistoryId;
    private String fileName;
    private String originalName;
    private String extension;
    private String filePath;
    private Long fileSize;


    public static WorkoutHistoryFileDto from(WorkoutHistoryFile file) {
        return WorkoutHistoryFileDto.builder()
                .id(file.getId())
                .workoutHistoryId(file.getWorkoutHistory().getWorkoutHistoryId())
                .fileName(file.getFileName())
                .originalName(file.getOriginalName())
                .extension(file.getExtension())
                .filePath(file.getFilePath())
                .fileSize(file.getFileSize())
                .build();
    }

}

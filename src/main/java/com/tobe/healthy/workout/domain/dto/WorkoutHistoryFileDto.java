package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
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
    private Long fileSize;
    private String fileUrl;


    public static WorkoutHistoryFileDto from(WorkoutHistoryFile file) {
        return WorkoutHistoryFileDto.builder()
                .id(file.getId())
                .workoutHistoryId(file.getWorkoutHistory().getWorkoutHistoryId())
                .fileName(file.getFileName())
                .originalName(file.getOriginalName())
                .extension(file.getExtension())
                .fileSize(file.getFileSize())
                .fileUrl(file.getFileUrl())
                .build();
    }

}

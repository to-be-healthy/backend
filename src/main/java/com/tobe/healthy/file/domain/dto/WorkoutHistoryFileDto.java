package com.tobe.healthy.file.domain.dto;

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

}

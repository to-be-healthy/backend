package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryFiles;
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
    private String fileUrl;
    private int fileOrder;


    public static WorkoutHistoryFileDto from(WorkoutHistoryFiles file) {
        return WorkoutHistoryFileDto.builder()
                .id(file.getId())
                .workoutHistoryId(file.getWorkoutHistory().getWorkoutHistoryId())
                .fileUrl(file.getFileUrl())
                .fileOrder(file.getFileOrder())
                .build();
    }

}

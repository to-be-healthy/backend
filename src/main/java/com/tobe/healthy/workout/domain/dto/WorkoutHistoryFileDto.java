package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.workoutHistory.WorkoutHistoryFiles;
import lombok.*;

@Data
@ToString
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

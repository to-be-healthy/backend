package com.tobe.healthy.workout.domain.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkoutHistoryCommandResult {

    private Long workoutHistoryId;
    private Long memberId;
    private String content;

}

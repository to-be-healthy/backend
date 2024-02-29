package com.tobe.healthy.workout.domain.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkoutHistoryAddCommandResult {

    private Long workoutHistoryId;
    private Long memberId;
    private String content;

}

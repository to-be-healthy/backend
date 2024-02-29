package com.tobe.healthy.workout.domain.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutHistoryAddCommand {

    private Long memberId;
    private String content;

}

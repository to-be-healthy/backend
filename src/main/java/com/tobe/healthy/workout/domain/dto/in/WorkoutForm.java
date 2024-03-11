package com.tobe.healthy.workout.domain.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutForm {

    private Long workoutId;
    private int set;
    private int weight;
    private int numberOfCycles;

}

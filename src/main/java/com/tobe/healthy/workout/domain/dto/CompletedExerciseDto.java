package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.CompletedExercise;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletedExerciseDto {

    private Long exerciseId;

    private String name;

    @Positive(message = "숫자를 입력해주세요.")
    private int setNum;

    @Positive(message = "숫자를 입력해주세요.")
    private int weight;

    @Positive(message = "숫자를 입력해주세요.")
    private int numberOfCycles;

    private Long workoutHistoryId;

    public static CompletedExerciseDto from(CompletedExercise exercise) {
        return CompletedExerciseDto.builder()
                .exerciseId(exercise.getExerciseId())
                .name((exercise.getName()))
                .setNum(exercise.getSetNum())
                .weight(exercise.getWeight())
                .numberOfCycles((exercise.getNumberOfCycles()))
                .workoutHistoryId(exercise.getWorkoutHistory().getWorkoutHistoryId())
                .build();
    }
}

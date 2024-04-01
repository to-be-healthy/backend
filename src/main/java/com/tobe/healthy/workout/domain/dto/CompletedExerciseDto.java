package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.CompletedExercise;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "운동 종류 ID", example = "1")
    private Long exerciseId;

    @Schema(description = "운동 종류 이름", example = "90/90 Hamstring")
    private String name;

    @Schema(description = "세트", example = "3")
    @Positive(message = "숫자를 입력해주세요.")
    private int setNum;

    @Schema(description = "무게", example = "20")
    @Positive(message = "숫자를 입력해주세요.")
    private int weight;

    @Schema(description = "반복횟수", example = "10")
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

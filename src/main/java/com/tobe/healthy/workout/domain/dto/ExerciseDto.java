package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.Exercise;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {

    private Long exerciseId;
    private String names;
    private ExerciseCategory category;
    private String muscles;

    public static ExerciseDto from(Exercise exercise) {
        String secondaryMuscles = exercise.getSecondaryMuscle() == null ? "" : ", " + exercise.getSecondaryMuscle();
        return ExerciseDto.builder()
                .exerciseId(exercise.getExerciseId())
                .names(exercise.getNames())
                .category(exercise.getCategory())
                .muscles(exercise.getPrimaryMuscle() + secondaryMuscles)
                .build();
    }

}

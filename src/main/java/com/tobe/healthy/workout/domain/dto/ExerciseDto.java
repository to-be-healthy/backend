package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCustom;
import lombok.*;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {

    private Long exerciseId;
    private Long exerciseCustomId;
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

    public static ExerciseDto from(ExerciseCustom custom) {
        return ExerciseDto.builder()
                .exerciseCustomId(custom.getExerciseCustomId())
                .names(custom.getNames())
                .category(custom.getCategory())
                .muscles(custom.getMuscles())
                .build();
    }

}

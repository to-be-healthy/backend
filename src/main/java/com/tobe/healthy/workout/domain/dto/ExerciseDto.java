package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.exercise.Exercise;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import lombok.*;
import org.apache.commons.lang3.StringUtils;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {

    private Long exerciseId;
    private String names;
    private ExerciseCategory category;
    private String muscles;
    private boolean custom;

    public static ExerciseDto from(Exercise exercise) {
        String muscles = "";
        if(exercise.getPrimaryMuscle() != null) muscles = exercise.getPrimaryMuscle() + ", ";
        if(exercise.getSecondaryMuscle() != null) muscles += exercise.getSecondaryMuscle() + ", ";
        return ExerciseDto.builder()
                .exerciseId(exercise.getExerciseId())
                .names(exercise.getNames())
                .category(exercise.getCategory())
                .muscles(StringUtils.removeEnd(muscles, ", "))
                .custom(exercise.getMember() != null)
                .build();
    }

}

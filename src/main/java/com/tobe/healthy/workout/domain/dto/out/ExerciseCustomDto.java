package com.tobe.healthy.workout.domain.dto.out;

import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCustom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCustomDto {

    private Long exerciseCustomId;
    private String names;
    private ExerciseCategory category;
    private String muscles;

    public static ExerciseCustomDto from(ExerciseCustom custom) {
        return ExerciseCustomDto.builder()
                .exerciseCustomId(custom.getExerciseCustomId())
                .names(custom.getNames())
                .category(custom.getCategory())
                .muscles(custom.getMuscles())
                .build();
    }

}

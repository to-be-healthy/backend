package com.tobe.healthy.workout.domain.dto.out;

import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class ExerciseCategoryDto {

    private String category;
    private String name;

    public static ExerciseCategoryDto from(ExerciseCategory exerciseCategory) {
        return ExerciseCategoryDto.builder()
                .category(exerciseCategory.getCode())
                .name(exerciseCategory.getDescription())
                .build();
    }
}

package com.tobe.healthy.workout.presentation.dto.out;

import com.tobe.healthy.workout.domain.ExerciseCategory;

public record ExerciseCategoryDto(
	String category,
	String name
) {

	public static ExerciseCategoryDto from(ExerciseCategory exerciseCategory) {
		return new ExerciseCategoryDto(
			exerciseCategory.getCode(),
			exerciseCategory.getDescription()
		);
	}
}

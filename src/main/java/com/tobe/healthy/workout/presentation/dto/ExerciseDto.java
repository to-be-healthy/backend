package com.tobe.healthy.workout.presentation.dto;

import org.apache.commons.lang3.StringUtils;

import com.tobe.healthy.workout.domain.Exercise;
import com.tobe.healthy.workout.domain.ExerciseCategory;

public record ExerciseDto(
	Long exerciseId,
	String names,
	ExerciseCategory category,
	String muscles,
	boolean custom
) {

	public static ExerciseDto from(Exercise exercise) {
		String muscles = "";
		if (exercise.getPrimaryMuscle() != null)
			muscles = exercise.getPrimaryMuscle() + ", ";
		if (exercise.getSecondaryMuscle() != null)
			muscles += exercise.getSecondaryMuscle() + ", ";
		return new ExerciseDto(
			exercise.getExerciseId(),
			exercise.getNames(),
			exercise.getCategory(),
			StringUtils.removeEnd(muscles, ", "),
			exercise.getMember() != null
		);
	}
}

package com.tobe.healthy.workout.presentation.dto;

import com.tobe.healthy.workout.domain.WorkoutHistoryFiles;

public record WorkoutHistoryFileDto(
	Long id,
	Long workoutHistoryId,
	String fileUrl,
	int fileOrder
) {

	public static WorkoutHistoryFileDto from(WorkoutHistoryFiles file) {
		return new WorkoutHistoryFileDto(
			file.getId(),
			file.getWorkoutHistory().getWorkoutHistoryId(),
			file.getFileUrl(),
			file.getFileOrder()
		);
	}
}

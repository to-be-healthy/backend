package com.tobe.healthy.gym.presentation.dto.out;

import com.tobe.healthy.gym.domain.Gym;

public record GymResult(
	Long gymId,
	String name
) {
	public static GymResult from(Gym gym) {
		return new GymResult(
			gym.getId(),
			gym.getName()
		);
	}
}

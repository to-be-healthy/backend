package com.tobe.healthy.gym.presentation.dto.out;

import com.tobe.healthy.gym.domain.Gym;

public record CommandRegisterGymResult(
	Long id,
	String name,
	String joinCode
) {
	public static CommandRegisterGymResult from(Gym gym) {
		return new CommandRegisterGymResult(
			gym.getId(),
			gym.getName(),
			gym.getJoinCode()
		);
	}
}

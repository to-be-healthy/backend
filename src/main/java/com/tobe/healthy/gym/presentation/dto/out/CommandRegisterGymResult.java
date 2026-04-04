package com.tobe.healthy.gym.presentation.dto.out;

import com.tobe.healthy.gym.domain.Gym;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRegisterGymResult {
	private Long id;
	private String name;
	private String joinCode;

	public static CommandRegisterGymResult from(Gym gym) {
		return CommandRegisterGymResult.builder()
			.id(gym.getId())
			.name(gym.getName())
			.joinCode(gym.getJoinCode())
			.build();
	}
}

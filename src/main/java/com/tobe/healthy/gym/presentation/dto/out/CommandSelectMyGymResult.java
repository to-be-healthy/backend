package com.tobe.healthy.gym.presentation.dto.out;

import com.tobe.healthy.gym.domain.entity.Gym;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandSelectMyGymResult {
	private Long id;
	private String name;

	public static CommandSelectMyGymResult from(Gym gym) {
		return CommandSelectMyGymResult.builder()
			.id(gym.getId())
			.name(gym.getName())
			.build();
	}
}

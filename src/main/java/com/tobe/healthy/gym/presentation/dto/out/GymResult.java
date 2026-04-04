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
public class GymResult {
	private Long gymId;
	private String name;

	public static GymResult from(Gym gym) {
		return GymResult.builder()
			.gymId(gym.getId())
			.name(gym.getName())
			.build();
	}
}

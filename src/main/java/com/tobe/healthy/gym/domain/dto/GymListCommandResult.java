package com.tobe.healthy.gym.domain.dto;

import com.tobe.healthy.gym.domain.entity.Gym;
import lombok.Data;

@Data
public class GymListCommandResult {
	private Long gymId;
	private String name;

	public GymListCommandResult(Gym gym) {
		this.gymId = gym.getId();
		this.name = gym.getName();
	}
}
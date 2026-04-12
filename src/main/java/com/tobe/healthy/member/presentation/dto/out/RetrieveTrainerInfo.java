package com.tobe.healthy.member.presentation.dto.out;

import com.tobe.healthy.gym.presentation.dto.out.GymDto;
import com.tobe.healthy.member.presentation.dto.ProfileDto;
import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.trainer.domain.TrainerMemberMapping;

public record RetrieveTrainerInfo(
	Long mappingId,
	TrainerInfo trainer
) {

	public static RetrieveTrainerInfo from(TrainerMemberMapping trainerMemberMapping) {
		return new RetrieveTrainerInfo(
			trainerMemberMapping.getMappingId(),
			TrainerInfo.from(trainerMemberMapping.getTrainer())
		);
	}

	public record TrainerInfo(
		Long id,
		String email,
		String name,
		ProfileDto profile,
		GymDto gym
	) {

		public static TrainerInfo from(Member trainer) {
			return new TrainerInfo(
				trainer.getId(),
				trainer.getEmail(),
				trainer.getName(),
				ProfileDto.from(trainer.getMemberProfile()),
				GymDto.from(trainer.getGym())
			);
		}
	}
}

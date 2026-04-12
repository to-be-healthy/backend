package com.tobe.healthy.trainer.presentation.dto;

import com.tobe.healthy.member.presentation.dto.MemberDto;
import com.tobe.healthy.trainer.domain.TrainerMemberMapping;

public record TrainerMemberMappingDto(
	Long mappingId,
	MemberDto trainer,
	MemberDto member
) {

	public static TrainerMemberMappingDto from(TrainerMemberMapping mapping) {
		return new TrainerMemberMappingDto(
			mapping.getMappingId(),
			MemberDto.from(mapping.getTrainer()),
			MemberDto.from(mapping.getMember())
		);
	}
}

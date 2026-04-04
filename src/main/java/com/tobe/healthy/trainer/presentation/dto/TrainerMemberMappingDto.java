package com.tobe.healthy.trainer.presentation.dto;

import com.tobe.healthy.member.presentation.dto.MemberDto;
import com.tobe.healthy.trainer.domain.TrainerMemberMapping;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class TrainerMemberMappingDto {

	private Long mappingId;
	private MemberDto trainer;
	private MemberDto member;

	public static TrainerMemberMappingDto from(TrainerMemberMapping mapping) {
		return TrainerMemberMappingDto.builder()
			.mappingId(mapping.getMappingId())
			.trainer(MemberDto.from(mapping.getTrainer()))
			.member(MemberDto.from(mapping.getMember()))
			.build();
	}
}

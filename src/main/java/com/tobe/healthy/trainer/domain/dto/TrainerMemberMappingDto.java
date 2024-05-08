package com.tobe.healthy.trainer.domain.dto;

import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerMemberMappingDto {

    private Long mappingId;
    private MemberDto trainer;
    private MemberDto member;

    public static TrainerMemberMappingDto from(TrainerMemberMapping mapping){
        return TrainerMemberMappingDto.builder()
                .mappingId(mapping.getMappingId())
                .trainer(MemberDto.from(mapping.getTrainer()))
                .member(MemberDto.from(mapping.getMember()))
                .build();
    }
}

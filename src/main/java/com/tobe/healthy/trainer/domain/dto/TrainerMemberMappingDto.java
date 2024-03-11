package com.tobe.healthy.trainer.domain.dto;

import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TrainerMemberMappingDto {

    private Long mappingId;
    private Long trainerId;
    private Long memberId;

    public static TrainerMemberMappingDto from(TrainerMemberMapping mapping){
        return TrainerMemberMappingDto.builder()
                .mappingId(mapping.getMappingId())
                .trainerId(mapping.getTrainerId())
                .memberId(mapping.getMemberId())
                .build();
    }

}

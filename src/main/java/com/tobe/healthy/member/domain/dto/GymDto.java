package com.tobe.healthy.member.domain.dto;

import com.tobe.healthy.member.domain.entity.Gym;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class GymDto {

    private Long id;
    private String name;

    public static GymDto from(Gym gym){
        return GymDto.builder()
                .id(gym.getId())
                .name(gym.getName())
                .build();
    }

}
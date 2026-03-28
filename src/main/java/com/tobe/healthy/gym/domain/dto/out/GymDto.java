package com.tobe.healthy.gym.domain.dto.out;

import com.tobe.healthy.gym.domain.entity.Gym;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymDto {
    private Long id;
    private String name;

    public static GymDto from(Gym gym) {
        return GymDto.builder()
                .id(gym.getId())
                .name(gym.getName())
                .build();
    }
}

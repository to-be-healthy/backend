package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class GymDto(
    val id: Long,
    val name: String
) {

    companion object {
        @JvmStatic
        fun from(gym: Gym): GymDto {
            return GymDto(
                id = gym.id,
                name = gym.name
            )
        }
    }
}

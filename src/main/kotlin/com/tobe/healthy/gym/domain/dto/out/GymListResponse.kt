package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class GymListResponse(
    val gymId: Long,
    val name: String
) {
    companion object {
        fun from(gym: Gym): GymListResponse {
            return GymListResponse(
                gymId = gym.id,
                name = gym.name
            )
        }
    }
}

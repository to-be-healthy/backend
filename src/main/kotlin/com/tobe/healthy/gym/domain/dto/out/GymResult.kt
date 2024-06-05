package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class GymResult(
    val gymId: Long?,
    val name: String
) {
    companion object {
        fun from(gym: Gym): GymResult {
            return GymResult(
                gymId = gym.id,
                name = gym.name
            )
        }
    }
}

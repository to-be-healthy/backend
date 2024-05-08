package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class GymListCommandResult(
    val gymId: Long,
    val name: String
) {
    companion object {
        fun from(gym: Gym): GymListCommandResult {
            return GymListCommandResult(
                gymId = gym.id,
                name = gym.name
            )
        }
    }
}

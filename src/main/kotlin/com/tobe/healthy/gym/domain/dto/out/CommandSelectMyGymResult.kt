package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class CommandSelectMyGymResult(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(gym: Gym): CommandSelectMyGymResult {
            return CommandSelectMyGymResult(
                id = gym.id,
                name = gym.name
            )
        }
    }
}

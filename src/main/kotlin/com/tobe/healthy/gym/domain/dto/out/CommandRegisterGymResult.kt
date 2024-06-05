package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class CommandRegisterGymResult(
    val id: Long?,
    val name: String,
    val joinCode: String
) {

    companion object {
        fun from(gym: Gym): CommandRegisterGymResult {
            return CommandRegisterGymResult(
                id = gym.id,
                name = gym.name,
                joinCode = gym.joinCode
            )
        }
    }
}

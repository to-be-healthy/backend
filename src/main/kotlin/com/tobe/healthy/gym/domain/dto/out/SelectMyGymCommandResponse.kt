package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class SelectMyGymCommandResponse(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(gym: Gym): SelectMyGymCommandResponse {
            return SelectMyGymCommandResponse(
                id = gym.id,
                name = gym.name
            )
        }
    }
}

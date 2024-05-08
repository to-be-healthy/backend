package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class SelectMyGymResponse(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(gym: Gym): SelectMyGymResponse {
            return SelectMyGymResponse(
                id = gym.id,
                name = gym.name
            )
        }
    }
}

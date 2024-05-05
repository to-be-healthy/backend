package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.gym.domain.entity.Gym

data class RegisterGymResponse(
    val id: Long,
    val name: String,
    val joinCode: Int
) {

    companion object {
        fun from(gym: Gym): RegisterGymResponse {
            return RegisterGymResponse(
                id = gym.id,
                name = gym.name,
                joinCode = gym.joinCode
            )
        }
    }
}

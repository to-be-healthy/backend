package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberProfile

data class TrainersOfGymResponse(
    val id: Long,
    val userId: String,
    val email: String,
    val name: String,
    val memberProfile: MemberProfile? = null
) {
    companion object {
        fun from(member: Member): TrainersOfGymResponse {
            return TrainersOfGymResponse(
                id = member.id,
                userId = member.userId,
                email = member.email,
                name = member.name,
                memberProfile = member.memberProfile
            )
        }
    }
}

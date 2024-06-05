package com.tobe.healthy.gym.domain.dto.out

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.member.domain.entity.MemberProfile

data class TrainersByGymResult(
    val id: Long,
    val userId: String,
    val email: String,
    val name: String,
    val profile: MemberProfileResult
) {
    companion object {
        fun from(member: Member): TrainersByGymResult {
            return TrainersByGymResult(
                id = member.id,
                userId = member.userId,
                email = member.email,
                name = member.name,
                profile = MemberProfileResult.from(member.memberProfile)
            )
        }
    }

    data class MemberProfileResult(
        val id: Long?,
        val fileUrl: String?
    ) {
        companion object {
            fun from(memberProfile: MemberProfile?): MemberProfileResult {
                return MemberProfileResult(
                    id = memberProfile?.id,
                    fileUrl = memberProfile?.fileUrl
                )
            }
        }
    }

}

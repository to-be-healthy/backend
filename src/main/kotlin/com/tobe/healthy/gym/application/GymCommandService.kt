package com.tobe.healthy.gym.application

import com.tobe.healthy.common.Utils
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.gym.domain.dto.out.CommandRegisterGymResult
import com.tobe.healthy.gym.domain.dto.out.CommandSelectMyGymResult
import com.tobe.healthy.gym.domain.entity.Gym
import com.tobe.healthy.gym.repository.GymRepository
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GymCommandService(
    private val memberRepository: MemberRepository,
    private val gymRepository: GymRepository,
) {
    fun selectMyGym(gymId: Long, joinCode: String?, memberId: Long): CommandSelectMyGymResult {

        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val gym = gymRepository.findByIdOrNull(gymId)
            ?: throw CustomException(GYM_NOT_FOUND)

        if (member.memberType == TRAINER) {
            if (gym.joinCode != joinCode) {
                throw CustomException(JOIN_CODE_NOT_VALID)
            }
        }

        if (member.gym?.id == gym.id) {
            throw CustomException(UNCHANGED_GYM_ID)
        }

        member.registerGym(gym)

        return CommandSelectMyGymResult.from(gym)
    }

    fun registerGym(name: String): CommandRegisterGymResult {
        gymRepository.findByName(name)?.let {
            throw CustomException(GYM_DUPLICATION)
        }

        val joinCode = Utils.getAuthCode(6)

        val gym = Gym.registerGym(name, joinCode)

        gymRepository.save(gym)

        return CommandRegisterGymResult.from(gym)
    }
}
package com.tobe.healthy.gym.application

import com.tobe.healthy.common.Utils
import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.*
import com.tobe.healthy.gym.domain.dto.out.GymListResponse
import com.tobe.healthy.gym.domain.dto.out.RegisterGymResponse
import com.tobe.healthy.gym.domain.dto.out.SelectMyGymCommandResponse
import com.tobe.healthy.gym.domain.dto.out.TrainersOfGymResponse
import com.tobe.healthy.gym.domain.entity.Gym
import com.tobe.healthy.gym.repository.GymRepository
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GymService(
    private val memberRepository: MemberRepository,
    private val gymRepository: GymRepository,
) {
    fun findAllGym(): List<GymListResponse> {
        return gymRepository.findAll().map { GymListResponse.from(it) }
    }

    fun selectMyGym(gymId: Long, joinCode: String?, memberId: Long): SelectMyGymCommandResponse {

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

        return SelectMyGymCommandResponse.from(gym)
    }

    fun registerGym(name: String): RegisterGymResponse {
        gymRepository.findByName(name)?.let {
            throw CustomException(GYM_DUPLICATION)
        }

        val joinCode = Utils.getAuthCode(6)

        val gym = Gym.registerGym(name, joinCode)

        gymRepository.save(gym)

        return RegisterGymResponse.from(gym)
    }

    fun findAllTrainersByGym(gymId: Long): List<TrainersOfGymResponse?> {
        return memberRepository.findAllTrainerByGym(gymId).map { TrainersOfGymResponse.from(it) }
    }
}
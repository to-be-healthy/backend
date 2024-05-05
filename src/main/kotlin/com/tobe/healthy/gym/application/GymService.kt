package com.tobe.healthy.gym.application

import com.tobe.healthy.config.error.CustomException
import com.tobe.healthy.config.error.ErrorCode.GYM_DUPLICATION
import com.tobe.healthy.config.error.ErrorCode.GYM_NOT_FOUND
import com.tobe.healthy.config.error.ErrorCode.JOIN_CODE_NOT_VALID
import com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND
import com.tobe.healthy.config.error.ErrorCode.UNCHANGED_GYM_ID
import com.tobe.healthy.gym.domain.dto.out.GymListCommandResult
import com.tobe.healthy.gym.domain.dto.out.RegisterGymResponse
import com.tobe.healthy.gym.domain.dto.out.TrainerCommandResult
import com.tobe.healthy.gym.domain.entity.Gym
import com.tobe.healthy.gym.repository.GymRepository
import com.tobe.healthy.member.domain.entity.MemberType.TRAINER
import com.tobe.healthy.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Random

@Service
@Transactional
class GymService(
    private val memberRepository: MemberRepository,
    private val gymRepository: GymRepository,
) {
    fun findAllGym(): List<GymListCommandResult> {
        return gymRepository.findAll().map { GymListCommandResult.from(it) }
    }

    fun selectMyGym(gymId: Long, joinCode: Int, memberId: Long): Boolean {

        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(MEMBER_NOT_FOUND)

        val gym = gymRepository.findByIdOrNull(gymId)
            ?: throw CustomException(GYM_NOT_FOUND)

        if (member.memberType == TRAINER) {
            if (gym.joinCode != joinCode) {
                throw CustomException(JOIN_CODE_NOT_VALID)
            }
        }

        if (member.gym.id == gym.id) {
            throw CustomException(UNCHANGED_GYM_ID)
        }

        member.registerGym(gym)

        return true
    }

    fun registerGym(name: String): RegisterGymResponse {
        gymRepository.findByName(name)?.let {
            throw CustomException(GYM_DUPLICATION)
        }

        val joinCode = getJoinCode()

        val gym = Gym.registerGym(name, joinCode)

        gymRepository.save(gym)

        return RegisterGymResponse.from(gym)
    }

    fun findAllTrainersByGym(gymId: Long): List<TrainerCommandResult?> {
        return memberRepository.findAllTrainerByGym(gymId).map { TrainerCommandResult.from(it) }
    }

    private fun getJoinCode(): Int {
        val random = Random()
        val builder = StringBuilder()
        var num = 0

        while (builder.length < 6) {
            num = random.nextInt(10)
            builder.append(num)
        }

        return builder.toString().toInt()
    }
}

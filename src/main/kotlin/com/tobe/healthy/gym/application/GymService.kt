package com.tobe.healthy.gym.application

import com.tobe.healthy.gym.domain.dto.out.GymResult
import com.tobe.healthy.gym.domain.dto.out.TrainersByGymResult
import com.tobe.healthy.gym.repository.GymRepository
import com.tobe.healthy.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GymService(
    private val memberRepository: MemberRepository,
    private val gymRepository: GymRepository,
) {
    fun findAllGym(): List<GymResult> {
        return gymRepository.findAll().map { GymResult.from(it) }
    }

    fun findAllTrainersByGym(gymId: Long): List<TrainersByGymResult?> {
        return memberRepository.findAllTrainerByGym(gymId).map { TrainersByGymResult.from(it) }
    }
}
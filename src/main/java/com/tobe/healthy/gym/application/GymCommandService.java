package com.tobe.healthy.gym.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tobe.healthy.common.Utils;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.error.ErrorCode;
import com.tobe.healthy.gym.presentation.dto.in.CommandRegisterGym;
import com.tobe.healthy.gym.presentation.dto.in.CommandSelectMyGym;
import com.tobe.healthy.gym.presentation.dto.out.CommandRegisterGymResult;
import com.tobe.healthy.gym.presentation.dto.out.CommandSelectMyGymResult;
import com.tobe.healthy.gym.domain.Gym;
import com.tobe.healthy.gym.repository.GymRepository;
import com.tobe.healthy.member.domain.Member;
import com.tobe.healthy.member.domain.MemberType;
import com.tobe.healthy.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GymCommandService {

	private final MemberRepository memberRepository;
	private final GymRepository gymRepository;

	public CommandRegisterGymResult registerGym(CommandRegisterGym request) {
		if (gymRepository.findByName(request.name()) != null) {
			throw new CustomException(ErrorCode.GYM_DUPLICATION);
		}

		String joinCode = Utils.getAuthCode(6);

		Gym gym = Gym.registerGym(request.name(), joinCode);

		gymRepository.save(gym);

		return CommandRegisterGymResult.from(gym);
	}

	public CommandSelectMyGymResult selectMyGym(Long gymId, CommandSelectMyGym request, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		Gym gym = gymRepository.findById(gymId)
			.orElseThrow(() -> new CustomException(ErrorCode.GYM_NOT_FOUND));

		if (member.getMemberType() == MemberType.TRAINER) {
			gym.validateJoinCode(request != null ? request.joinCode() : null);
		}

		member.registerGym(gym);

		return CommandSelectMyGymResult.from(gym);
	}
}

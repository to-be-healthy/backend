package com.tobe.healthy.gym.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tobe.healthy.gym.presentation.dto.out.GymResult;
import com.tobe.healthy.gym.presentation.dto.out.TrainersByGymResult;
import com.tobe.healthy.gym.repository.GymRepository;
import com.tobe.healthy.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GymService {

	private final MemberRepository memberRepository;
	private final GymRepository gymRepository;

	public List<GymResult> findAllGym() {
		return gymRepository.findAll().stream()
			.map(GymResult::from)
			.collect(Collectors.toList());
	}

	public List<TrainersByGymResult> findAllTrainersByGym(Long gymId) {
		return memberRepository.findAllTrainerByGym(gymId).stream()
			.map(TrainersByGymResult::from)
			.collect(Collectors.toList());
	}
}

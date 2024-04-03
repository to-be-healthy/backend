package com.tobe.healthy.gym.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.gym.domain.dto.out.GymListCommandResult;
import com.tobe.healthy.gym.domain.dto.out.TrainerCommandResult;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.gym.repository.GymRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tobe.healthy.config.error.ErrorCode.GYM_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GymService {

	private final MemberRepository memberRepository;
	private final GymRepository gymRepository;

	public List<GymListCommandResult> findAllGym() {
		return gymRepository.findAll()
				.stream()
				.map(GymListCommandResult::new)
				.collect(toList());
	}

	public Boolean selectMyGym(Long gymId, Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Gym gym = gymRepository.findById(gymId)
				.orElseThrow(() -> new CustomException(GYM_NOT_FOUND));

		member.registerGym(gym);

		return true;
	}

	public Boolean registerGym(String name) {
		Gym gym = Gym.builder()
				.name(name)
				.build();
		gymRepository.save(gym);
		return true;
	}

	public List<TrainerCommandResult> findAllTrainersByGym(Long gymId) {
		return memberRepository.findAllTrainerByGym(gymId).stream()
			.map(TrainerCommandResult::new)
			.toList();
	}

}
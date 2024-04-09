package com.tobe.healthy.gym.application;

import static com.tobe.healthy.config.error.ErrorCode.GYM_DUPLICATION;
import static com.tobe.healthy.config.error.ErrorCode.GYM_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.JOIN_CODE_NOT_VALID;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;
import static java.util.stream.Collectors.toList;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.gym.domain.dto.out.GymListCommandResult;
import com.tobe.healthy.gym.domain.dto.out.TrainerCommandResult;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.gym.repository.GymRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public Boolean selectMyGym(Long gymId, Integer joinCode, Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Gym gym = gymRepository.findById(gymId)
				.orElseThrow(() -> new CustomException(GYM_NOT_FOUND));

		if (member.getMemberType().equals(TRAINER)) {
			if (ObjectUtils.isEmpty(joinCode) || gym.getJoinCode() != joinCode) {
				throw new CustomException(JOIN_CODE_NOT_VALID);
			}
		}

		member.registerGym(gym);

		return true;
	}

	public Boolean registerGym(String name) {
		gymRepository.findByName(name).ifPresent(gym -> {
			throw new CustomException(GYM_DUPLICATION);
		});

		int joinCode = getJoinCode();

		Gym gym = Gym.registerGym(name, joinCode);

		gymRepository.save(gym);

		return true;
	}

	public List<TrainerCommandResult> findAllTrainersByGym(Long gymId) {
		return memberRepository.findAllTrainerByGym(gymId).stream()
			.map(TrainerCommandResult::new)
			.toList();
	}

	private int getJoinCode() {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder();
		int num = 0;

		while (buffer.length() < 6) {
			num = random.nextInt(10);
			buffer.append(num);
		}

		return Integer.parseInt(buffer.toString());
	}
}
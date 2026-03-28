package com.tobe.healthy.trainer.respository;

import java.util.List;
import java.util.Optional;

import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;

public interface TrainerMemberMappingRepositoryCustom {
	List<Long> findAllTrainerIds();

	Optional<TrainerMemberMapping> findTrainerInfoByMemberId(Long memberId);
}

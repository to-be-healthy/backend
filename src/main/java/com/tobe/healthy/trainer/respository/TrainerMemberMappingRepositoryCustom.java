package com.tobe.healthy.trainer.respository;

import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;

import java.util.List;
import java.util.Optional;

public interface TrainerMemberMappingRepositoryCustom {
    List<Long> findAllTrainerIds();
    Optional<TrainerMemberMapping> findTrainerInfoByMemberId(Long memberId);
}

package com.tobe.healthy.trainer.respository;

import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerMemberMappingRepository extends JpaRepository<TrainerMemberMapping, Long> {

    Optional<TrainerMemberMapping> findByTrainerIdAndMemberId(Long trainerid, Long memberId);
    Optional<TrainerMemberMapping> findTop1ByMemberIdOrderByCreatedAtDesc(Long memberId);

}

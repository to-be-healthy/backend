package com.tobe.healthy.trainer.respository;

import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerMemberMappingRepository extends JpaRepository<TrainerMemberMapping, Long>, TrainerMemberMappingRepositoryCustom {

    Optional<TrainerMemberMapping> findByTrainerIdAndMemberId(Long trainerId, Long memberId);
    Optional<TrainerMemberMapping> findTop1ByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<TrainerMemberMapping> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);
    List<TrainerMemberMapping> findAllByTrainerId(Long trainerId);

}

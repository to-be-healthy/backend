package com.tobe.healthy.trainer.respository;

import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerMemberMappingRepository extends JpaRepository<TrainerMemberMapping, Long> {

    Optional<TrainerMemberMapping> findByTrainerIdAndMemberId(@Param("trainerId") Long trainerId, @Param("memberId") Long memberId);
    Optional<TrainerMemberMapping> findTop1ByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId);

}

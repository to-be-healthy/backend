package com.tobe.healthy.trainer.respository;

import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrainerMemberMappingRepository extends JpaRepository<TrainerMemberMapping, Long> {

    Optional<TrainerMemberMapping> findByTrainerIdAndMemberId(Long trainerId, Long memberId);
    Optional<TrainerMemberMapping> findTop1ByMemberIdOrderByCreatedAtDesc(Long memberId);
    void deleteByMemberId(Long memberId);
}

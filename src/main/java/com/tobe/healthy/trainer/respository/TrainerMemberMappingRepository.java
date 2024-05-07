package com.tobe.healthy.trainer.respository;

import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerMemberMappingRepository extends JpaRepository<TrainerMemberMapping, Long>, TrainerMemberMappingRepositoryCustom {
    Optional<TrainerMemberMapping> findByTrainerIdAndMemberId(Long trainerId, Long memberId);
    @EntityGraph(attributePaths = {"trainer", "member"})
    Optional<TrainerMemberMapping> findTop1ByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<TrainerMemberMapping> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);
    List<TrainerMemberMapping> findAllByTrainerId(Long trainerId);
    void deleteByTrainerIdAndMemberId(Long trainerId, Long memberId);
}

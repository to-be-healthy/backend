package com.tobe.healthy.diet.repository;

import com.tobe.healthy.diet.domain.entity.Diet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietRepository extends JpaRepository<Diet, Long>, DietRepositoryCustom {
    Optional<Diet> findByDietIdAndDelYnFalse(Long dietId);
    Optional<Diet> findByDietIdAndMemberIdAndDelYnFalse(Long dietId, Long memberId);

}

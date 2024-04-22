package com.tobe.healthy.diet.repository;

import com.tobe.healthy.diet.domain.entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DietRepository extends JpaRepository<Diet, Long>, DietRepositoryCustom {
    Optional<Diet> findByDietIdAndDelYnFalse(Long dietId);
}

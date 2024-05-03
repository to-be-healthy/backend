package com.tobe.healthy.diet.repository;

import com.tobe.healthy.diet.domain.entity.DietFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietFileRepository extends JpaRepository<DietFiles, Long> {

}

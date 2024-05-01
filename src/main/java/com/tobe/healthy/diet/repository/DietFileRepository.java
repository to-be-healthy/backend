package com.tobe.healthy.diet.repository;

import com.tobe.healthy.file.domain.entity.DietFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietFileRepository extends JpaRepository<DietFile, Long> {

}

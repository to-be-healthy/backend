package com.tobe.healthy.diet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.diet.domain.entity.DietFiles;

public interface DietFileRepository extends JpaRepository<DietFiles, Long> {

}

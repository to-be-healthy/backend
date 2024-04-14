package com.tobe.healthy.diet.repository;

import com.tobe.healthy.file.domain.entity.DietFile;

import java.time.LocalDateTime;
import java.util.List;

public interface DietRepositoryCustom {
    List<DietFile> findAllCreateAtToday(Long memberId, LocalDateTime start, LocalDateTime end);
}

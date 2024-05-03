package com.tobe.healthy.diet.repository;

import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietFiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface DietRepositoryCustom {

    List<DietFiles> findAllCreateAtToday(Long memberId, LocalDateTime start, LocalDateTime end);
    Page<Diet> getDietOfMonth(Long memberId, Pageable pageable, String searchDate);
    List<DietFiles> getDietFile(List<Long> ids);
    Diet findTop1ByCreateAtToday(Long memberId, LocalDateTime start, LocalDateTime end);

}

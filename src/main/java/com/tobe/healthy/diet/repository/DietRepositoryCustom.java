package com.tobe.healthy.diet.repository;

import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.file.domain.entity.DietFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface DietRepositoryCustom {

    List<DietFile> findAllCreateAtToday(Long memberId, LocalDateTime start, LocalDateTime end);
    Page<Diet> getDietOfMonth(Long memberId, Pageable pageable, String searchDate);
    List<DietFile> getDietFile(List<Long> ids);

}

package com.tobe.healthy.diet.repository;

import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietFiles;
import com.tobe.healthy.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DietRepositoryCustom {

    Page<Diet> getDietOfMonth(Long memberId, Pageable pageable, String searchDate);

    List<DietFiles> getDietFile(List<Long> ids);

    Diet findTop1ByCreateAtToday(Long memberId);

    Page<Diet> getDietByTrainer(Member trainer, Pageable pageable, String searchDate);

    Diet getDietCreatedAtToday(Long memberId);

}

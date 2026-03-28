package com.tobe.healthy.diet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tobe.healthy.diet.domain.entity.DietComment;

public interface DietCommentRepositoryCustom {

	Page<DietComment> getCommentsByDietId(Long dietId, Pageable pageable);
}

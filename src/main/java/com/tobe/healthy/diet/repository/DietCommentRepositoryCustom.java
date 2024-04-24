package com.tobe.healthy.diet.repository;

import com.tobe.healthy.diet.domain.entity.DietComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DietCommentRepositoryCustom {

    Page<DietComment> getCommentsByDietId(Long dietId, Pageable pageable);
}

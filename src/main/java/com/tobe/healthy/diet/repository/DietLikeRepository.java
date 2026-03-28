package com.tobe.healthy.diet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.diet.domain.entity.DietLike;
import com.tobe.healthy.diet.domain.entity.DietLikePK;

public interface DietLikeRepository extends JpaRepository<DietLike, DietLikePK>, DietLikeRepositoryCustom {

}

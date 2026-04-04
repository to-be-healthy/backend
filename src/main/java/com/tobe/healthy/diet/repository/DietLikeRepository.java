package com.tobe.healthy.diet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.diet.domain.DietLike;
import com.tobe.healthy.diet.domain.DietLikePK;

public interface DietLikeRepository extends JpaRepository<DietLike, DietLikePK>, DietLikeRepositoryCustom {

}

package com.tobe.healthy.diet.repository;

import com.tobe.healthy.diet.domain.entity.DietLike;
import com.tobe.healthy.diet.domain.entity.DietLikePK;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DietLikeRepository extends JpaRepository<DietLike, DietLikePK>, DietLikeRepositoryCustom {

}

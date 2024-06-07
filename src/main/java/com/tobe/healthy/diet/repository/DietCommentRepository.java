package com.tobe.healthy.diet.repository;


import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DietCommentRepository extends JpaRepository<DietComment, Long>, DietCommentRepositoryCustom {

    Long countByDiet(Diet diet);
    Optional<DietComment> findByCommentIdAndDelYnFalse(@Param("commentId") Long parentCommentId);
    Optional<DietComment> findByCommentIdAndMemberIdAndDelYnFalse(Long commentId, Long memberId);
    Long countByDietAndDelYnFalse(Diet diet);
    Long countByDietAndMemberIdAndDelYnFalse(Diet diet, Long trainerId);

}

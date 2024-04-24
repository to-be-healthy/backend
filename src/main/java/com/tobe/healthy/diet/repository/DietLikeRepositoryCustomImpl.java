package com.tobe.healthy.diet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import static com.tobe.healthy.diet.domain.entity.QDietLike.dietLike;
import static com.tobe.healthy.workout.domain.entity.QWorkoutHistoryLike.workoutHistoryLike;


@Repository
@RequiredArgsConstructor
public class DietLikeRepositoryCustomImpl implements DietLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long getLikeCnt(Long dietId) {
        return queryFactory
                .select(dietLike.count())
                .from(dietLike)
                .where(dietIdEq(dietId))
                .fetchOne();
    }

    @Override
    public void deleteLikeByDietId(Long dietId) {
        queryFactory.delete(dietLike)
                .where(dietIdEq(dietId))
                .execute();
    }

    private BooleanExpression dietIdEq(Long dietId) {
        if (!ObjectUtils.isEmpty(dietId)){
            return dietLike.dietLikePK.diet.dietId.eq(dietId);
        }
        return null;
    }

}

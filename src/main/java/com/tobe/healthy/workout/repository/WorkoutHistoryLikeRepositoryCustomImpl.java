package com.tobe.healthy.workout.repository;

import static com.tobe.healthy.workout.domain.entity.QWorkoutHistoryLike.workoutHistoryLike;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;


@Repository
@RequiredArgsConstructor
public class WorkoutHistoryLikeRepositoryCustomImpl implements WorkoutHistoryLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long getLikeCnt(Long workoutHistoryId) {
        return queryFactory
                .select(workoutHistoryLike.count())
                .from(workoutHistoryLike)
                .where(workoutHistoryIdEq(workoutHistoryId))
                .fetchOne();
    }

    @Override
    public void deleteLikeByWorkoutHistoryId(Long workoutHistoryId) {
        queryFactory.delete(workoutHistoryLike)
                .where(workoutHistoryIdEq(workoutHistoryId))
                .execute();
    }

    private BooleanExpression workoutHistoryIdEq(Long workoutHistoryId) {
        if (!ObjectUtils.isEmpty(workoutHistoryId)){
            return workoutHistoryLike.workoutHistoryLikePK.workoutHistory.workoutHistoryId.eq(workoutHistoryId);
        }
        return null;
    }

}

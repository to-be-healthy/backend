package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistoryLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;



@Repository
@RequiredArgsConstructor
public class WorkoutHistoryLikeRepositoryCustomImpl implements WorkoutHistoryLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private QWorkoutHistoryLike qLike = QWorkoutHistoryLike.workoutHistoryLike;

    @Override
    public Long getLikeCnt(Long workoutHistoryId) {
        return queryFactory
                .select(qLike.count())
                .from(qLike)
                .where(workoutHistoryIdEq(workoutHistoryId))
                .fetchOne();
    }

    @Override
    public void deleteLikeByWorkoutHistoryId(Long workoutHistoryId) {
        queryFactory.delete(qLike)
                .where(workoutHistoryIdEq(workoutHistoryId))
                .execute();
    }

    private BooleanExpression workoutHistoryIdEq(Long workoutHistoryId) {
        if (!ObjectUtils.isEmpty(workoutHistoryId)){
            return qLike.workoutHistoryLikePK.workoutHistory.workoutHistoryId.eq(workoutHistoryId);
        }
        return null;
    }

}

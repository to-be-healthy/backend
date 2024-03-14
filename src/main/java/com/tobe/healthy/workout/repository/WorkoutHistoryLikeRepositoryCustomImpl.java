package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistoryComment;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistoryLike;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;


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
                .where(qLike.workoutHistoryLikePK.workoutHistory.workoutHistoryId.eq(workoutHistoryId))
                .fetchOne();
    }

    @Override
    public void deleteLikeByWorkoutHistoryId(Long workoutHistoryId) {
        queryFactory.delete(qLike)
                .where(qLike.workoutHistoryLikePK.workoutHistory.workoutHistoryId.eq(workoutHistoryId))
                .execute();
    }

}

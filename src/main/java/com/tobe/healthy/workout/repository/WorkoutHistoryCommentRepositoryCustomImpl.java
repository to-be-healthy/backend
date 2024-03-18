package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistoryComment;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class WorkoutHistoryCommentRepositoryCustomImpl implements WorkoutHistoryCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private QWorkoutHistoryComment qComment = QWorkoutHistoryComment.workoutHistoryComment;
    private QMember qMember = QMember.member;

    @Override
    public Page<WorkoutHistoryComment> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(qComment.count())
                .from(qComment)
                .where(historyIdEq(workoutHistoryId))
                .fetchOne();
        List<WorkoutHistoryComment> comments =  queryFactory
                .select(qComment)
                .from(qComment)
                .where(historyIdEq(workoutHistoryId))
                .orderBy(qComment.orderNum.asc(), qComment.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(comments, pageable, ()-> totalCnt );
    }

    private BooleanExpression historyIdEq(Long workoutHistoryId) {
        if (!ObjectUtils.isEmpty(workoutHistoryId)){
            return qComment.workoutHistory.workoutHistoryId.eq(workoutHistoryId);
        }
        return null;
    }
}

package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMemberProfile;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.member.domain.entity.QMember.member;
import static com.tobe.healthy.workout.domain.entity.QWorkoutHistoryComment.workoutHistoryComment;


@Repository
@RequiredArgsConstructor
public class WorkoutHistoryCommentRepositoryCustomImpl implements WorkoutHistoryCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<WorkoutHistoryComment> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable) {
        QMemberProfile profileId = new QMemberProfile("profileId");
        Long totalCnt = queryFactory
                .select(workoutHistoryComment.count())
                .from(workoutHistoryComment)
                .leftJoin(workoutHistoryComment.member, member)
                .leftJoin(member.memberProfile, profileId)
                .where(historyIdEq(workoutHistoryId))
                .fetchOne();
        List<WorkoutHistoryComment> comments =  queryFactory
                .select(workoutHistoryComment)
                .from(workoutHistoryComment)
                .leftJoin(workoutHistoryComment.member, member).fetchJoin()
                .leftJoin(member.memberProfile, profileId).fetchJoin()
                .where(historyIdEq(workoutHistoryId))
                .orderBy(workoutHistoryComment.orderNum.asc(), workoutHistoryComment.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(comments, pageable, ()-> totalCnt );
    }

    private BooleanExpression historyIdEq(Long workoutHistoryId) {
        if (!ObjectUtils.isEmpty(workoutHistoryId)){
            return workoutHistoryComment.workoutHistory.workoutHistoryId.eq(workoutHistoryId);
        }
        return null;
    }
}

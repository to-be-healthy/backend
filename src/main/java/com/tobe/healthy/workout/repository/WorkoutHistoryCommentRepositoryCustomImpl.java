package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryCommentDto;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistoryComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class WorkoutHistoryCommentRepositoryCustomImpl implements WorkoutHistoryCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private QWorkoutHistoryComment qComment = QWorkoutHistoryComment.workoutHistoryComment;
    private QMember qMember = QMember.member;

    @Override
    public Page<WorkoutHistoryCommentDto> getCommentsByWorkoutHistoryId(Long workoutHistoryId, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(qComment.count())
                .from(qComment)
                .where(qComment.workoutHistory.workoutHistoryId.eq(workoutHistoryId), qComment.delYn.eq(false))
                .fetchOne();
        List<WorkoutHistoryCommentDto> comments =  queryFactory
                .select(Projections.fields(WorkoutHistoryCommentDto.class,
                        qComment.commentId,
                        qComment.content,
                        qComment.createdAt,
                        qComment.updatedAt,
                        qComment.member.name
                ))
                .from(qComment)
                .where(qComment.workoutHistory.workoutHistoryId.eq(workoutHistoryId), qComment.delYn.eq(false))
                .orderBy(qComment.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(comments, pageable, ()-> totalCnt );
    }
}

package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.file.domain.entity.QWorkoutHistoryFile;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.file.domain.entity.QWorkoutHistoryFile.workoutHistoryFile;
import static com.tobe.healthy.workout.domain.entity.QWorkoutHistory.workoutHistory;

@Repository
@RequiredArgsConstructor
public class WorkoutHistoryRepositoryCustomImpl implements WorkoutHistoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<WorkoutHistory> getWorkoutHistory(Long memberId, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(workoutHistory.count())
                .from(workoutHistory)
                .where(workoutHistory.member.id.eq(memberId), historyDeYnEq(false))
                .fetchOne();
        List<WorkoutHistory> workoutHistories =  queryFactory
                .select(workoutHistory)
                .from(workoutHistory)
                .where(workoutHistory.member.id.eq(memberId), historyDeYnEq(false))
                .orderBy(workoutHistory.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(workoutHistories, pageable, ()-> totalCnt );
    }

    @Override
    public Page<WorkoutHistory> getWorkoutHistoryByTrainer(Long trainerId, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(workoutHistory.count())
                .from(workoutHistory)
                .where(workoutHistory.trainerId.eq(trainerId), historyDeYnEq(false))
                .fetchOne();
        List<WorkoutHistory> workoutHistories =  queryFactory
                .select(workoutHistory)
                .from(workoutHistory)
                .where(workoutHistory.trainerId.eq(trainerId), historyDeYnEq(false))
                .orderBy(workoutHistory.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(workoutHistories, pageable, ()-> totalCnt );
    }

    @Override
    public List<WorkoutHistoryFile> getWorkoutHistoryFile(List<Long> ids) {
        return queryFactory.select(workoutHistoryFile)
                .from(workoutHistoryFile)
                .where(workoutHistoryFile.workoutHistory.workoutHistoryId.in(ids), historyFileDeYnEq(false))
                .orderBy(workoutHistoryFile.createdAt.desc())
                .fetch();
    }

    private BooleanExpression historyDeYnEq(boolean bool) {
        return workoutHistory.delYn.eq(bool);
    }

    private BooleanExpression historyFileDeYnEq(boolean bool) {
        return workoutHistoryFile.delYn.eq(bool);
    }

}

package com.tobe.healthy.workout.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.file.domain.dto.WorkoutHistoryFileDto;
import com.tobe.healthy.file.domain.entity.QWorkoutHistoryFile;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistory;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;

import static com.tobe.healthy.file.domain.entity.QWorkoutHistoryFile.workoutHistoryFile;
import static com.tobe.healthy.workout.domain.entity.QWorkoutHistory.workoutHistory;

@Repository
@RequiredArgsConstructor
public class WorkoutHistoryRepositoryCustomImpl implements WorkoutHistoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<WorkoutHistory> getWorkoutHistoryOfMonth(Long memberId, Pageable pageable, String searchDate) {
        Long totalCnt = queryFactory
                .select(workoutHistory.count())
                .from(workoutHistory)
                .where(workoutHistory.member.id.eq(memberId), historyDeYnEq(false), convertDateFormat(searchDate))
                .fetchOne();
        List<WorkoutHistory> workoutHistories =  queryFactory
                .select(workoutHistory)
                .from(workoutHistory)
                .where(workoutHistory.member.id.eq(memberId), historyDeYnEq(false), convertDateFormat(searchDate))
                .orderBy(workoutHistory.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(workoutHistories, pageable, ()-> totalCnt );
    }

    @Override
    public Page<WorkoutHistory> getWorkoutHistoryByTrainer(Member trainer, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(workoutHistory.count())
                .from(workoutHistory)
                .where(workoutHistory.trainer.id.eq(trainer.getId()), historyDeYnEq(false))
                .fetchOne();
        List<WorkoutHistory> workoutHistories =  queryFactory
                .select(workoutHistory)
                .from(workoutHistory)
                .where(workoutHistory.trainer.id.eq(trainer.getId()), historyDeYnEq(false))
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

    private BooleanExpression convertDateFormat(String searchDate) {
        if (ObjectUtils.isEmpty(searchDate)) return null;
        StringTemplate stringTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , workoutHistory.createdAt
                , ConstantImpl.create("%Y-%m"));
        return stringTemplate.eq(searchDate);
    }

}

package com.tobe.healthy.workout.repository.workoutHistory;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMemberProfile;
import com.tobe.healthy.workout.domain.dto.out.QWorkoutHistoryDto;
import com.tobe.healthy.workout.domain.dto.out.WorkoutHistoryDto;
import com.tobe.healthy.workout.domain.entity.workoutHistory.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.diet.domain.entity.QDietComment.dietComment;
import static com.tobe.healthy.member.domain.entity.QMember.member;
import static com.tobe.healthy.workout.domain.entity.workoutHistory.QWorkoutHistory.workoutHistory;
import static com.tobe.healthy.workout.domain.entity.workoutHistory.QWorkoutHistoryFiles.workoutHistoryFiles;
import static com.tobe.healthy.workout.domain.entity.workoutHistory.QWorkoutHistoryLike.workoutHistoryLike;


@Repository
@RequiredArgsConstructor
public class WorkoutHistoryRepositoryCustomImpl implements WorkoutHistoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<WorkoutHistoryDto> getWorkoutHistoryOfMonth(Long loginMemberId, Long memberId, Pageable pageable, String searchDate) {
        QMemberProfile profileId = new QMemberProfile("profileId");
        Long totalCnt = queryFactory
                .select(workoutHistory.count())
                .from(workoutHistory)
                .where(memberIdEq(memberId), historyDeYnEq(false), convertDateFormat(searchDate))
                .fetchOne();
        List<WorkoutHistoryDto> workoutHistories = queryFactory
                .select(new QWorkoutHistoryDto(workoutHistory.workoutHistoryId, workoutHistory.content, workoutHistory.member
                        , isLiked()
                        , workoutHistory.likeCnt, workoutHistory.commentCnt, workoutHistory.viewMySelf, member.memberProfile))
                .from(workoutHistory)
                .leftJoin(workoutHistory.member, member)
                .leftJoin(member.memberProfile, profileId)
                .leftJoin(workoutHistoryLike)
                .on(workoutHistory.workoutHistoryId.eq(workoutHistoryLike.workoutHistoryLikePK.workoutHistory.workoutHistoryId)
                        , workoutHistoryLike.workoutHistoryLikePK.member.id.eq(loginMemberId))
                .where(memberIdEq(memberId), historyDeYnEq(false), convertDateFormat(searchDate))
                .orderBy(workoutHistory.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(workoutHistories, pageable, ()-> totalCnt );
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return workoutHistory.member.id.eq(memberId);
    }

    @Override
    public Page<WorkoutHistoryDto> getWorkoutHistoryByGym(Long loginMemberId, Long gymId, Pageable pageable, String searchDate) {
        QMemberProfile profileId = new QMemberProfile("profileId");
        Long totalCnt = queryFactory
                .select(workoutHistory.count())
                .from(workoutHistory)
                .where(gymIdEq(gymId)
                        , historyDeYnEq(false)
                        , convertDateFormat(searchDate)
                        , viewMySelfEq(false))
                .fetchOne();
        List<WorkoutHistoryDto> workoutHistories = queryFactory
                .select(new QWorkoutHistoryDto(workoutHistory.workoutHistoryId, workoutHistory.content, workoutHistory.member
                        , isLiked()
                        , workoutHistory.likeCnt, workoutHistory.commentCnt, workoutHistory.viewMySelf, member.memberProfile))
                .from(workoutHistory)
                .leftJoin(workoutHistory.member, member)
                .leftJoin(member.memberProfile, profileId)
                .leftJoin(workoutHistoryLike)
                .on(workoutHistory.workoutHistoryId.eq(workoutHistoryLike.workoutHistoryLikePK.workoutHistory.workoutHistoryId)
                        , workoutHistoryLike.workoutHistoryLikePK.member.id.eq(loginMemberId))
                .where(gymIdEq(gymId)
                        , historyDeYnEq(false)
                        , convertDateFormat(searchDate)
                        , viewMySelfEq(false))
                .orderBy(workoutHistory.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(workoutHistories, pageable, ()-> totalCnt );
    }

    private BooleanExpression gymIdEq(Long gymId) {
        return workoutHistory.gym.id.eq(gymId);
    }

    @Override
    public List<WorkoutHistoryFiles> getWorkoutHistoryFile(List<Long> ids) {
        return queryFactory.select(workoutHistoryFiles)
                .from(workoutHistoryFiles)
                .where(historyIdIn(ids), historyFileDeYnEq(false))
                .orderBy(workoutHistoryFiles.createdAt.asc(), workoutHistoryFiles.fileOrder.asc())
                .fetch();
    }

    private BooleanExpression historyIdIn(List<Long> ids) {
        return workoutHistoryFiles.workoutHistory.workoutHistoryId.in(ids);
    }

    private BooleanExpression isLiked() {
        return new CaseBuilder()
                .when(workoutHistoryLike.workoutHistoryLikePK.member.id.isNotNull())
                .then(true)
                .otherwise(false).as("is_liked");
    }

    private BooleanExpression historyDeYnEq(boolean bool) {
        return workoutHistory.delYn.eq(bool);
    }

    private BooleanExpression viewMySelfEq(boolean bool) {
        return workoutHistory.viewMySelf.eq(bool);
    }

    private BooleanExpression historyFileDeYnEq(boolean bool) {
        return workoutHistoryFiles.delYn.eq(bool);
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

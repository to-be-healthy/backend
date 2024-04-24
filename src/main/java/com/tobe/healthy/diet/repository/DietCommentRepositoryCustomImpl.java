package com.tobe.healthy.diet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.diet.domain.entity.DietComment;
import com.tobe.healthy.file.domain.entity.QProfile;
import com.tobe.healthy.workout.domain.entity.WorkoutHistoryComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.diet.domain.entity.QDietComment.dietComment;
import static com.tobe.healthy.member.domain.entity.QMember.member;
import static com.tobe.healthy.workout.domain.entity.QWorkoutHistoryComment.workoutHistoryComment;


@Repository
@RequiredArgsConstructor
public class DietCommentRepositoryCustomImpl implements DietCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DietComment> getCommentsByDietId(Long dietId, Pageable pageable) {
        QProfile profileId = new QProfile("profileId");
        Long totalCnt = queryFactory
                .select(dietComment.count())
                .from(dietComment)
                .leftJoin(dietComment.member, member)
                .leftJoin(member.profileId, profileId)
                .where(dietIdEq(dietId))
                .fetchOne();
        List<DietComment> comments =  queryFactory
                .select(dietComment)
                .from(dietComment)
                .leftJoin(dietComment.member, member).fetchJoin()
                .leftJoin(member.profileId, profileId).fetchJoin()
                .where(dietIdEq(dietId))
                .orderBy(dietComment.orderNum.asc(), dietComment.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(comments, pageable, ()-> totalCnt );
    }

    private BooleanExpression dietIdEq(Long dietId) {
        if (!ObjectUtils.isEmpty(dietId)){
            return dietComment.diet.dietId.eq(dietId);
        }
        return null;
    }
}

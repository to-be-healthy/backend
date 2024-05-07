package com.tobe.healthy.diet.repository;

import static com.tobe.healthy.diet.domain.entity.QDietComment.dietComment;
import static com.tobe.healthy.member.domain.entity.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.diet.domain.entity.DietComment;
import com.tobe.healthy.member.domain.entity.QMemberProfile;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;


@Repository
@RequiredArgsConstructor
public class DietCommentRepositoryCustomImpl implements DietCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DietComment> getCommentsByDietId(Long dietId, Pageable pageable) {
        QMemberProfile profileId = new QMemberProfile("profileId");
        Long totalCnt = queryFactory
                .select(dietComment.count())
                .from(dietComment)
                .leftJoin(dietComment.member, member)
                .leftJoin(member.memberProfile, profileId)
                .where(dietIdEq(dietId))
                .fetchOne();
        List<DietComment> comments =  queryFactory
                .select(dietComment)
                .from(dietComment)
                .leftJoin(dietComment.member, member).fetchJoin()
                .leftJoin(member.memberProfile, profileId).fetchJoin()
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

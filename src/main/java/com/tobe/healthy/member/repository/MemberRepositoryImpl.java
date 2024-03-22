package com.tobe.healthy.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import static com.tobe.healthy.member.domain.entity.QGym.gym;
import static com.tobe.healthy.member.domain.entity.QMember.member;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Member findByMemberIdWithGym(Long trainerId) {
        return queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.gym, gym).fetchJoin()
                .where(memberIdEq(trainerId), member.delYn.eq(false))
                .fetchOne();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (!ObjectUtils.isEmpty(memberId)) {
            return member.id.eq(memberId);
        }
        return null;
    }
}

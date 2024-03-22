package com.tobe.healthy.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.QGym;
import com.tobe.healthy.member.domain.entity.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;



@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private QMember qMember = QMember.member;
    private QGym qGym = QGym.gym;

    @Override
    public Member findByMemberIdWithGym(Long trainerId) {
        return queryFactory
                .select(qMember)
                .from(qMember)
                .leftJoin(qMember.gym, qGym).fetchJoin()
                .where(memberIdEq(trainerId), qMember.delYn.eq(false))
                .fetchOne();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (!ObjectUtils.isEmpty(memberId)) {
            return qMember.id.eq(memberId);
        }
        return null;
    }
}

package com.tobe.healthy.member.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.gym.domain.entity.QGymMembership;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.trainer.domain.entity.QTrainerMemberMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;


import java.util.List;

import static com.tobe.healthy.file.domain.entity.QProfile.profile;
import static com.tobe.healthy.gym.domain.entity.QGym.gym;
import static com.tobe.healthy.gym.domain.entity.QGymMembership.gymMembership;
import static com.tobe.healthy.member.domain.entity.QMember.member;
import static com.tobe.healthy.trainer.domain.entity.QTrainerMemberMapping.trainerMemberMapping;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Member findByMemberIdWithGym(Long trainerId) {
        Tuple tuple = queryFactory
                .select(member, gym)
                .from(member)
                .leftJoin(member.gym, gym).fetchJoin()
                .where(memberIdEq(trainerId), member.delYn.eq(false))
                .fetchOne();
        Member m = tuple.get(member);
        m.registerGym(tuple.get(gym));
        return m;
    }

    @Override
    public Member findByMemberIdWithProfile(Long memberId) {
        Tuple tuple = queryFactory
                .select(member, profile)
                .from(member)
                .leftJoin(member.profileId, profile).fetchJoin()
                .where(memberIdEq(memberId), member.delYn.eq(false))
                .fetchOne();
        Member m = tuple.get(member);
        m.registerProfile(tuple.get(profile));
        return m;
    }

    @Override
    public List<Tuple> findAllMyMemberInTrainer(Long trainerId) {
//        List<Tuple> tuples = queryFactory
//                .select(member, trainerMemberMapping, gymMembership)
//                .from(member)
//                .innerJoin(trainerMemberMapping).fetchJoin()
//                .on(member.id.eq(trainerMemberMapping.memberId))
//                .leftJoin(gymMembership).fetchJoin()
//                .on(member.id.eq(gymMembership.member.id))
//                .where(trainerMemberMapping.trainerId.eq(trainerId)
//                        , member.memberType.eq(MemberType.STUDENT)
//                        , member.delYn.eq(false))
//                .orderBy(member.id.asc())
//                .fetch();
//        return tuples;
        return null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (!ObjectUtils.isEmpty(memberId)) {
            return member.id.eq(memberId);
        }
        return null;
    }
}

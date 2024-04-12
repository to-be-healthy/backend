package com.tobe.healthy.member.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.gym.domain.dto.MemberInTeamDto;
import com.tobe.healthy.gym.domain.dto.QMemberInTeamDto;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;


import java.util.List;

import static com.tobe.healthy.file.domain.entity.QProfile.profile;
import static com.tobe.healthy.gym.domain.entity.QGym.gym;
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

    public List<MemberInTeamDto> findAllMyMemberInTeam(Long trainerId, String searchValue, String sortValue, Pageable pageable) {
        return queryFactory
                .select(new QMemberInTeamDto(member.id, member.name, member.userId, member.email,
                        trainerMemberMapping.ranking, trainerMemberMapping.lessonCnt, trainerMemberMapping.remainLessonCnt,
                        member.nickname, profile.fileUrl))
                .from(trainerMemberMapping)
                .innerJoin(trainerMemberMapping.member, member)
                .on(trainerMemberMapping.member.id.eq(member.id))
                .leftJoin(member.profileId, profile)
                .on(profile.member.profileId.eq(member.profileId))
                .where(trainerMemberMapping.trainer.id.eq(trainerId)
                        , member.memberType.eq(MemberType.STUDENT)
                        , member.delYn.eq(false)
                        , nameLike(searchValue))
                .orderBy(sortBy(sortValue))
                .fetch();
    }

    @Override
    public Page<Member> findAllUnattachedMembers(String searchValue, String sortValue, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(member.count())
                .from(member)
                .where(member.memberType.eq(MemberType.STUDENT)
                        , member.delYn.eq(false)
                        , nameLike(searchValue)
                        , JPAExpressions.selectFrom(trainerMemberMapping)
                                .where(trainerMemberMapping.member.eq(member))
                                .notExists())
                .fetchOne();
        List<Member> members = queryFactory
                .select(member)
                .from(member)
                .where(member.memberType.eq(MemberType.STUDENT)
                        , member.delYn.eq(false)
                        , nameLike(searchValue)
                        , JPAExpressions.selectFrom(trainerMemberMapping)
                                .where(trainerMemberMapping.member.eq(member))
                                .notExists())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(members, pageable, ()-> totalCnt );
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (!ObjectUtils.isEmpty(memberId)) {
            return member.id.eq(memberId);
        }
        return null;
    }

    private BooleanExpression nameLike(String name) {
        if (!ObjectUtils.isEmpty(name)) {
            return member.name.containsIgnoreCase(name);
        }
        return null;
    }

    private OrderSpecifier sortBy(String sortValue) {
        if (!ObjectUtils.isEmpty(sortValue)) {
            if("ranking".equals(sortValue)) return trainerMemberMapping.ranking.asc();
        }
        return member.id.asc();
    }
}

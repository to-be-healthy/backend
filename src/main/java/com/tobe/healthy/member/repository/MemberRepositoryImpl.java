package com.tobe.healthy.member.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.dto.out.MemberDetailResult;
import com.tobe.healthy.member.domain.dto.out.MemberInTeamResult;
import com.tobe.healthy.member.domain.dto.out.QMemberDetailResult;
import com.tobe.healthy.member.domain.dto.out.QMemberInTeamResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.tobe.healthy.course.domain.entity.QCourse.course;
import static com.tobe.healthy.gym.domain.entity.QGym.gym;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;
import static com.tobe.healthy.member.domain.entity.QMember.member;
import static com.tobe.healthy.member.domain.entity.QMemberProfile.memberProfile;
import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED;
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
    public Member findByMemberIdWithProfileAndGym(Long memberId) {
        Tuple tuple = queryFactory
                .select(member, memberProfile, gym)
                .from(member)
                .leftJoin(member.memberProfile, memberProfile).fetchJoin()
                .leftJoin(member.gym, gym).fetchJoin()
                .where(memberIdEq(memberId), member.delYn.eq(false))
                .fetchOne();
        Member m = tuple.get(member);
        m.registerProfile(tuple.get(memberProfile));
        m.registerGym(tuple.get(gym));
        return m;
    }

    public List<MemberInTeamResult> findAllMyMemberInTeam(Long trainerId, String searchValue, String sortValue, Pageable pageable) {
        return queryFactory
                .select(new QMemberInTeamResult(member.id, member.name, member.userId, member.email,
                        trainerMemberMapping.ranking, course.totalLessonCnt, course.remainLessonCnt,
                        member.nickname, memberProfile.fileUrl))
                .from(trainerMemberMapping)
                .innerJoin(trainerMemberMapping.member, member)
                .on(trainerMemberMapping.member.id.eq(member.id))
                .leftJoin(member.memberProfile, memberProfile)
                .on(memberProfile.member.memberProfile.eq(member.memberProfile))
                .leftJoin(course)
                .on(course.member.id.eq(member.id), course.remainLessonCnt.gt(0))
                .where(trainerMemberMapping.trainer.id.eq(trainerId)
                        , member.memberType.eq(MemberType.STUDENT)
                        , member.delYn.eq(false)
                        , nameLike(searchValue))
                .orderBy(sortBy(sortValue))
                .fetch();
    }

    @Override
    public Page<Member> findAllUnattachedMembers(Long gymId, String searchValue, String sortValue, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(member.count())
                .from(member)
                .where(member.memberType.eq(MemberType.STUDENT)
                        , member.delYn.eq(false)
                        , nameLike(searchValue)
                        , member.gym.id.eq(gymId)
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
                        , member.gym.id.eq(gymId)
                        , JPAExpressions.selectFrom(trainerMemberMapping)
                                .where(trainerMemberMapping.member.eq(member))
                                .notExists())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(members, pageable, () -> totalCnt);
    }

    @Override
    public MemberDetailResult getMemberOfTrainer(Long memberId) {
        return queryFactory
                .select(new QMemberDetailResult(member.id, member.name, member.nickname, memberProfile.fileUrl
                        , trainerMemberMapping.memo, trainerMemberMapping.ranking
                        , schedule.lessonDt, schedule.lessonStartTime))
                .from(member)
                .leftJoin(memberProfile)
                .on(member.memberProfile.id.eq(memberProfile.id))
                .leftJoin(trainerMemberMapping)
                .on(member.id.eq(trainerMemberMapping.member.id))
                .leftJoin(schedule)
                .on(member.id.eq(schedule.applicant.id)
                        , schedule.delYn.eq(false)
                        , schedule.reservationStatus.eq(COMPLETED)
                        , lessonDateTimeAfterToday())
                .where(memberIdEq(memberId), member.delYn.eq(false))
                .orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public List<Member> findAllTrainerByGym(Long gymId) {
        return queryFactory.select(member)
                .from(member)
                .leftJoin(member.memberProfile).fetchJoin()
                .where(
                        member.gym.id.eq(gymId),
                        member.memberType.eq(TRAINER),
                        member.delYn.eq(false)
                )
                .orderBy(member.id.desc())
                .fetch();
    }

    private Predicate lessonDateTimeAfterToday() {
        return schedule.lessonDt.after(LocalDate.now())
                .or(schedule.lessonDt.goe(LocalDate.now()).and(schedule.lessonStartTime.after(LocalTime.now())));
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
            if ("ranking".equals(sortValue)) return trainerMemberMapping.ranking.asc();
        }
        return member.id.asc();
    }

}

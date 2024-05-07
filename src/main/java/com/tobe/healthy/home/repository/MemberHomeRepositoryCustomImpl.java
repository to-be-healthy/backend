package com.tobe.healthy.home.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.applicationform.domain.entity.QApplicationForm;
import com.tobe.healthy.common.ResultFormatType;
import com.tobe.healthy.workout.domain.entity.QWorkoutHistory;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberHomeRepositoryCustomImpl implements MemberHomeRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QApplicationForm qAppliform = QApplicationForm.applicationForm;
    private final QWorkoutHistory qWorkoutHistory = QWorkoutHistory.workoutHistory;

    @Override
    public long getAttendanceOfMonth(long memberId, LocalDate startDay, LocalDate endDay) {
        List<String> ptDates = queryFactory.select(Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , qAppliform.createdAt
                , ConstantImpl.create("%Y-%m-%d"))).distinct()
                .from(qAppliform)
                .where(qAppliform.memberId.eq(memberId),
                        qAppliform.completed.eq(ResultFormatType.SUCCESS.getResultAlphabet()),
                        qAppliform.createdAt.between(startDay.atStartOfDay(), endDay.atStartOfDay()))
                .fetch();

        List<String> wkDates = queryFactory.select(Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , qWorkoutHistory.createdAt
                , ConstantImpl.create("%Y-%m-%d"))).distinct()
                .from(qWorkoutHistory)
                .where(qWorkoutHistory.member.id.eq(memberId),
                        qWorkoutHistory.createdAt.between(startDay.atStartOfDay(), endDay.atStartOfDay()))
                .fetch();

        Set<String> ptDateSet = new LinkedHashSet<>(ptDates);
        ptDateSet.addAll(wkDates);
        return ptDateSet.size();
    }
}

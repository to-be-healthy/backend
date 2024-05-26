package com.tobe.healthy.course.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.course.domain.entity.CourseHistory;
import com.tobe.healthy.course.domain.entity.CourseHistoryType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.course.domain.entity.CourseHistoryType.ONE_LESSON;
import static com.tobe.healthy.course.domain.entity.QCourse.course;
import static com.tobe.healthy.course.domain.entity.QCourseHistory.courseHistory;

@Repository
@RequiredArgsConstructor
public class CourseHistoryRepositoryCustomImpl implements CourseHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CourseHistory> getCourseHistory(Long memberId, Long trainerId, Pageable pageable, String searchDate) {
        Long totalCnt = queryFactory
                .select(courseHistory.count())
                .from(courseHistory)
                .leftJoin(courseHistory.course, course)
                .where(courseMemberIdEq(memberId), convertDateFormat(searchDate))
                .fetchOne();
        List<CourseHistory> courseHistories =  queryFactory
                .select(courseHistory)
                .from(courseHistory)
                .leftJoin(courseHistory.course, course)
                .where(courseMemberIdEq(memberId), courseTrainerIdEq(trainerId), convertDateFormat(searchDate))
                .orderBy(courseHistory.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(courseHistories, pageable, ()-> totalCnt );
    }

    @Override
    public Long checkPaidOneLesson(Long trainerId, String searchDate) {
        return queryFactory
                .select(courseHistory.count())
                .from(courseHistory)
                .where(courseTrainerIdEq(trainerId), convertDateFormat(searchDate), typeEq(ONE_LESSON))
                .fetchOne();
    }

    private BooleanExpression typeEq(CourseHistoryType type) {
        if(!ObjectUtils.isEmpty(type)){
            return courseHistory.type.eq(type);
        }
        return null;
    }

    private BooleanExpression courseMemberIdEq(Long memberId) {
        if (!ObjectUtils.isEmpty(memberId)){
            return course.member.id.eq(memberId);
        }
        return null;
    }

    private BooleanExpression courseTrainerIdEq(Long trainerId) {
        if (!ObjectUtils.isEmpty(trainerId)){
            return courseHistory.trainer.id.eq(trainerId);
        }
        return null;
    }

    private BooleanExpression convertDateFormat(String searchDate) {
        if (ObjectUtils.isEmpty(searchDate)) return null;
        StringTemplate stringTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , courseHistory.createdAt
                , ConstantImpl.create("%Y-%m"));
        return stringTemplate.eq(searchDate);
    }

}

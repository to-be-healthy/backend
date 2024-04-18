package com.tobe.healthy.point.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import com.tobe.healthy.point.domain.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.point.domain.entity.QPoint.point1;


@Repository
@RequiredArgsConstructor
public class PointRepositoryCustomImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Point> getPoint(Long memberId, String searchDate, Pageable pageable) {
        Long totalCnt = queryFactory
                .select(point1.count())
                .from(point1)
                .where(point1.member.id.eq(memberId), convertDateFormat(searchDate))
                .fetchOne();
        List<com.tobe.healthy.point.domain.entity.Point> points =  queryFactory
                .select(point1)
                .from(point1)
                .where(point1.member.id.eq(memberId), convertDateFormat(searchDate))
                .orderBy(point1.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(points, pageable, ()-> totalCnt );
    }

    private BooleanExpression convertDateFormat(String searchDate) {
        if (ObjectUtils.isEmpty(searchDate)) return null;
        StringTemplate stringTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , point1.createdAt
                , ConstantImpl.create("%Y-%m"));
        return stringTemplate.eq(searchDate);
    }

}

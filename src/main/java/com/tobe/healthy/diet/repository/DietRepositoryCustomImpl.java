package com.tobe.healthy.diet.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.tobe.healthy.diet.domain.entity.QDiet.diet;
import static com.tobe.healthy.diet.domain.entity.QDietFile.dietFile;


@Repository
@RequiredArgsConstructor
public class DietRepositoryCustomImpl implements DietRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DietFile> findAllCreateAtToday(Long memberId, LocalDateTime start, LocalDateTime end) {
        return queryFactory.select(dietFile)
                .from(dietFile)
                .where(JPAExpressions.selectFrom(diet)
                        .where(diet.dietId.eq(dietFile.diet.dietId)
                        , createdAtBetween(start, end)
                        , delYnEq(false)
                        , memberIdEq(memberId))
                        .orderBy(diet.createdAt.desc())
                        .limit(1)
                        .exists())
                .fetch();
    }

    @Override
    public Page<Diet> getDietOfMonth(Long memberId, Pageable pageable, String searchDate) {
        Long totalCnt = queryFactory
                .select(diet.count())
                .from(diet)
                .where(memberIdEq(memberId), delYnEq(false), convertDateFormat(searchDate))
                .fetchOne();
        List<Diet> diets =  queryFactory
                .select(diet)
                .from(diet)
                .where(memberIdEq(memberId), delYnEq(false), convertDateFormat(searchDate))
                .orderBy(diet.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(diets, pageable, ()-> totalCnt );
    }

    @Override
    public List<DietFile> getDietFile(List<Long> ids) {
        return queryFactory.select(dietFile)
                .from(dietFile)
                .where(dietFile.diet.dietId.in(ids), dietFileDeYnEq(false))
                .orderBy(dietFile.createdAt.desc())
                .fetch();
    }

    @Override
    public Diet findTop1ByCreateAtToday(Long memberId, LocalDateTime start, LocalDateTime end) {
        return queryFactory.select(diet)
                .from(diet)
                .where(createdAtBetween(start, end)
                        , delYnEq(false)
                        , memberIdEq(memberId))
                .orderBy(diet.createdAt.desc())
                .limit(1)
                .fetchOne();
    }

    private BooleanExpression dietFileDeYnEq(boolean bool) {
        return dietFile.delYn.eq(bool);
    }

    private BooleanExpression convertDateFormat(String searchDate) {
        if (ObjectUtils.isEmpty(searchDate)) return null;
        StringTemplate stringTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , diet.createdAt
                , ConstantImpl.create("%Y-%m"));
        return stringTemplate.eq(searchDate);
    }

    private BooleanExpression delYnEq(boolean bool) {
        return diet.delYn.eq(bool);
    }

    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        if (!ObjectUtils.isEmpty(start) && !ObjectUtils.isEmpty(end)) {
            return diet.createdAt.between(start, end);
        }
        return null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return diet.member.id.eq(memberId);
    }

}

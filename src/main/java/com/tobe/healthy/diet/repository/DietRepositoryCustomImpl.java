package com.tobe.healthy.diet.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.diet.domain.entity.Diet;
import com.tobe.healthy.diet.domain.entity.DietFiles;
import com.tobe.healthy.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.tobe.healthy.diet.domain.entity.QDiet.diet;
import static com.tobe.healthy.diet.domain.entity.QDietFiles.dietFiles;


@Repository
@RequiredArgsConstructor
public class DietRepositoryCustomImpl implements DietRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Diet getTodayDiet(Long memberId) {
        return queryFactory.select(diet)
                .from(diet)
                .where(convertEatDate_YYYY_MM_DD(LocalDate.now().toString())
                        , delYnEq(false)
                        , memberIdEq(memberId))
                .orderBy(diet.createdAt.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public List<String> getDietUploadDays(Long memberId, String searchDate) {
        return queryFactory
                .select(Expressions.stringTemplate(
                        "DATE_FORMAT({0}, {1})"
                        , diet.eatDate
                        , ConstantImpl.create("%Y-%m-%d"))).distinct()
                .from(diet)
                .where(memberIdEq(memberId), delYnEq(false), convertEatDate_YYYY_MM(searchDate))
                .orderBy(diet.eatDate.asc())
                .fetch();
    }

    @Override
    public Page<Diet> getDietOfMonth(Long memberId, Pageable pageable, String searchDate) {
        Long totalCnt = queryFactory
                .select(diet.count())
                .from(diet)
                .where(memberIdEq(memberId), delYnEq(false), convertEatDate_YYYY_MM(searchDate))
                .fetchOne();
        List<Diet> diets = queryFactory
                .select(diet)
                .from(diet)
                .where(memberIdEq(memberId), delYnEq(false), convertEatDate_YYYY_MM(searchDate))
                .orderBy(diet.eatDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(diets, pageable, () -> totalCnt);
    }

    @Override
    public List<DietFiles> getDietFile(List<Long> ids) {
        return queryFactory.select(dietFiles)
                .from(dietFiles)
                .where(dietFiles.diet.dietId.in(ids), dietFileDeYnEq(false))
                .orderBy(dietFiles.createdAt.desc())
                .fetch();
    }

    @Override
    public Page<Diet> getDietByTrainer(Member trainer, Pageable pageable, String searchDate) {
        Long totalCnt = queryFactory
                .select(diet.count())
                .from(diet)
                .where(dietTrainerIdEq(trainer), dietDeYnEq(false), convertDateFormat_YYYY_MM_DD(searchDate))
                .fetchOne();
        List<Diet> diets = queryFactory
                .select(diet)
                .from(diet)
                .where(dietTrainerIdEq(trainer), dietDeYnEq(false), convertDateFormat_YYYY_MM_DD(searchDate))
                .orderBy(diet.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(diets, pageable, () -> totalCnt);
    }

    private BooleanExpression dietTrainerIdEq(Member trainer) {
        return diet.trainer.id.eq(trainer.getId());
    }

    private BooleanExpression dietDeYnEq(boolean bool) {
        return diet.delYn.eq(bool);
    }

    private BooleanExpression dietFileDeYnEq(boolean bool) {
        return dietFiles.delYn.eq(bool);
    }

    private BooleanExpression convertEatDate_YYYY_MM(String searchDate) {
        if (ObjectUtils.isEmpty(searchDate)) return null;
        StringTemplate stringTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , diet.eatDate
                , ConstantImpl.create("%Y-%m"));
        return stringTemplate.eq(searchDate);
    }

    private BooleanExpression convertEatDate_YYYY_MM_DD(String searchDate) {
        if (ObjectUtils.isEmpty(searchDate)) return null;
        StringTemplate stringTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , diet.eatDate
                , ConstantImpl.create("%Y-%m-%d"));
        return stringTemplate.eq(searchDate);
    }

    private BooleanExpression convertDateFormat_YYYY_MM_DD(String searchDate) {
        if (ObjectUtils.isEmpty(searchDate)) return null;
        StringTemplate stringTemplate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , diet.createdAt
                , ConstantImpl.create("%Y-%m-%d"));
        return stringTemplate.eq(searchDate);
    }

    private BooleanExpression delYnEq(boolean bool) {
        return diet.delYn.eq(bool);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return diet.member.id.eq(memberId);
    }

}

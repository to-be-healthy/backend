package com.tobe.healthy.diet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.file.domain.entity.DietFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.tobe.healthy.diet.domain.entity.QDiet.diet;
import static com.tobe.healthy.file.domain.entity.QDietFile.dietFile;


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

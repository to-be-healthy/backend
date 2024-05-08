package com.tobe.healthy.trainer.respository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tobe.healthy.trainer.domain.entity.QTrainerMemberMapping.trainerMemberMapping;


@Repository
@RequiredArgsConstructor
public class TrainerMemberMappingRepositoryCustomImpl implements TrainerMemberMappingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findAllTrainerIds() {
        return queryFactory
                .select(trainerMemberMapping.trainer.id)
                .from(trainerMemberMapping)
                .groupBy(trainerMemberMapping.trainer)
                .orderBy(trainerMemberMapping.trainer.id.asc())
                .fetch();
    }
}

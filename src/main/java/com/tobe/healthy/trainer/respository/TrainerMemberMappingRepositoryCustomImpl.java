package com.tobe.healthy.trainer.respository;

import static com.tobe.healthy.trainer.domain.entity.QTrainerMemberMapping.trainerMemberMapping;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


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

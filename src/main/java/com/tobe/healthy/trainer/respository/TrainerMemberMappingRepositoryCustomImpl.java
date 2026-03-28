package com.tobe.healthy.trainer.respository;

import static com.tobe.healthy.member.domain.entity.QMember.*;
import static com.tobe.healthy.trainer.domain.entity.QTrainerMemberMapping.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;

import lombok.RequiredArgsConstructor;

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

	@Override
	public Optional<TrainerMemberMapping> findTrainerInfoByMemberId(Long memberId) {
		TrainerMemberMapping result = queryFactory
			.select(trainerMemberMapping)
			.from(trainerMemberMapping)
			.leftJoin(trainerMemberMapping.trainer, member).fetchJoin()
			.leftJoin(member.gym).fetchJoin()
			.leftJoin(member.memberProfile).fetchJoin()
			.where(
				trainerMemberMapping.member.id.eq(memberId)
			)
			.fetchOne();
		return Optional.ofNullable(result);
	}
}

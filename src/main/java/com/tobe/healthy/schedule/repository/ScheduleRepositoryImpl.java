package com.tobe.healthy.schedule.repository;

import static com.querydsl.core.types.Projections.constructor;
import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ScheduleCommandResult> findAllSchedule() {
		QMember trainer = new QMember("trainer");
		QMember applicant = new QMember("applicant");

		return queryFactory
			.select(constructor(ScheduleCommandResult.class,
				schedule.id,
				schedule.startDt,
				schedule.endDt,
				schedule.reservationStatus,
				schedule.round,
				trainer.name,
				applicant.name))
			.from(schedule)
			.leftJoin(schedule.trainer, trainer)
			.leftJoin(schedule.applicant, applicant)
			.fetch();
	}
}
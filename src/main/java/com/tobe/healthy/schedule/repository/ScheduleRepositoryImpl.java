package com.tobe.healthy.schedule.repository;

import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;
import static com.tobe.healthy.schedule.domain.entity.QStandBySchedule.standBySchedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ScheduleCommandResult> findAllSchedule() {
		QMember trainer = new QMember("trainer");
		QMember applicant = new QMember("applicant");

		List<Schedule> fetch = queryFactory
				.select(schedule)
				.from(schedule)
				.leftJoin(schedule.trainer, trainer).fetchJoin()
				.leftJoin(schedule.applicant, applicant).fetchJoin()
				.leftJoin(schedule.standBySchedule, standBySchedule).fetchJoin()
				.orderBy(schedule.lessonDt.asc(), schedule.round.asc())
				.fetch();

		return fetch.stream()
			.map(ScheduleCommandResult::of)
			.collect(Collectors.toList());
	}
}
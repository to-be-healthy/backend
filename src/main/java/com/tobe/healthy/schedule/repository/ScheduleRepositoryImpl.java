package com.tobe.healthy.schedule.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;
import static com.tobe.healthy.schedule.domain.entity.QStandBySchedule.standBySchedule;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond) {
		QMember trainer = new QMember("trainer");
		QMember applicant = new QMember("applicant");

		List<Schedule> fetch = queryFactory
				.select(schedule)
				.from(schedule)
				.leftJoin(schedule.trainer, trainer).fetchJoin()
				.leftJoin(schedule.applicant, applicant).fetchJoin()
				.leftJoin(schedule.standBySchedule, standBySchedule).fetchJoin()
				.where(lessonDtEq(searchCond), lessonDtBetween(searchCond), delYnFalse(), standByScheduleDelYnFalse())
				.orderBy(schedule.lessonDt.asc(), schedule.round.asc())
				.fetch();

		return fetch.stream()
			.map(ScheduleCommandResult::from)
			.collect(toList());
	}

	private BooleanExpression standByScheduleDelYnFalse() {
		return standBySchedule.delYn.isFalse();
	}

	private BooleanExpression delYnFalse() {
		return schedule.delYn.eq(false);
	}

	@Override
	public List<ScheduleCommandResult> findAllByApplicantId(Long memberId) {
		QMember trainer = new QMember("trainer");
		List<Schedule> fetch = queryFactory
			.select(schedule)
			.from(schedule)
			.leftJoin(schedule.trainer, trainer).fetchJoin()
			.leftJoin(schedule.standBySchedule, standBySchedule).fetchJoin()
			.where(schedule.applicant.id.eq(memberId), scheduleDelYnFalse(), standByScheduleDelYnFalse())
			.orderBy(schedule.lessonDt.desc(), schedule.round.desc())
			.fetch();
		return fetch.stream()
			.map(ScheduleCommandResult::from)
			.collect(toList());
	}

	private BooleanExpression scheduleDelYnFalse() {
		return schedule.delYn.isFalse();
	}

	private BooleanExpression lessonDtBetween(ScheduleSearchCond searchCond) {
		if (!ObjectUtils.isEmpty(searchCond.getLessonStartDt()) && !ObjectUtils.isEmpty(searchCond.getLessonEndDt())) {
			return schedule.lessonDt.between(searchCond.getLessonStartDt(), searchCond.getLessonEndDt());
		}
		return null;
	}

	private BooleanExpression lessonDtEq(ScheduleSearchCond searchCond) {
		if (!ObjectUtils.isEmpty(searchCond.getLessonDt())) {
			return schedule.lessonDt.eq(searchCond.getLessonDt());
		}
		return null;
	}
}

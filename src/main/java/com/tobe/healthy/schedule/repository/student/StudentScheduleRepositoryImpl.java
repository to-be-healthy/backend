package com.tobe.healthy.schedule.repository.student;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;
import static com.tobe.healthy.schedule.domain.entity.QScheduleWaiting.scheduleWaiting;
import static java.util.stream.Collectors.toList;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.entity.in.ScheduleSearchCond;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StudentScheduleRepositoryImpl implements StudentScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Long trainerId, Member member) {
		List<Schedule> results = queryFactory
				.select(schedule)
				.from(schedule)
				.leftJoin(schedule.trainer, new QMember("trainer")).fetchJoin()
				.leftJoin(schedule.applicant, new QMember("applicant")).fetchJoin()
				.leftJoin(schedule.scheduleWaiting, scheduleWaiting).on(scheduleWaiting.delYn.isFalse())
				.where(lessonDtEq(searchCond), lessonDtBetween(searchCond), delYnFalse(), schedule.trainer.id.eq(trainerId))
				.orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
				.fetch();

		return results.stream()
				.map(result -> ScheduleCommandResult.from(result, member))
				.collect(toList());
	}

	private BooleanExpression scheduleWaitingDelYnFalse() {
		return scheduleWaiting.delYn.isFalse();
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
			.leftJoin(schedule.scheduleWaiting, scheduleWaiting).fetchJoin()
			.where(schedule.applicant.id.eq(memberId), scheduleDelYnFalse(), scheduleWaitingDelYnFalse())
			.orderBy(schedule.lessonDt.desc(), schedule.lessonStartTime.asc())
			.fetch();
		return fetch.stream()
			.map(ScheduleCommandResult::from)
			.collect(toList());
	}

	@Override
	public List<MyReservation> findAllMyReservation(Long memberId, ScheduleSearchCond searchCond) {
		List<Schedule> schedules = queryFactory.select(schedule)
				.from(schedule)
				.innerJoin(schedule.applicant, new QMember("applicant")).fetchJoin()
				.innerJoin(schedule.trainer, new QMember("trainer")).fetchJoin()
				.where(schedule.applicant.id.eq(memberId), schedule.lessonDt.goe(LocalDate.now()), lessonDtEq(searchCond))
				.orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
				.fetch();
		return schedules.stream().map(MyReservation::from).collect(toList());
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
			StringExpression formattedDate = stringTemplate("DATE_FORMAT({0}, '%Y%m')", schedule.lessonDt);
			return formattedDate.eq(searchCond.getLessonDt());
		}
		return null;
	}
}

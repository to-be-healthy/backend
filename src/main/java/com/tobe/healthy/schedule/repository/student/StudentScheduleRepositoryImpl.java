package com.tobe.healthy.schedule.repository.student;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.schedule.domain.dto.in.StudentScheduleCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;
import static com.tobe.healthy.schedule.domain.entity.QScheduleWaiting.scheduleWaiting;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.*;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StudentScheduleRepositoryImpl implements StudentScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ScheduleCommandResult> findAllSchedule(StudentScheduleCond searchCond, Long trainerId, Member member) {
		List<Schedule> results = queryFactory
				.select(schedule)
				.from(schedule)
				.leftJoin(schedule.trainer, new QMember("trainer")).fetchJoin()
				.leftJoin(schedule.applicant, new QMember("applicant")).fetchJoin()
				.leftJoin(schedule.scheduleWaiting, scheduleWaiting)
				.where(lessonDtEq(searchCond), lessonDtBetween(searchCond), scheduleTrainerIdEq(trainerId)
				, scheduleReservationStatusForStudent())
				.orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
				.fetch();

		return results.stream()
				.map(result -> ScheduleCommandResult.from(result, member))
				.collect(toList());
	}

	@Override
	public List<ScheduleCommandResult> findAllByApplicantId(Long memberId) {
		QMember trainer = new QMember("trainer");
		List<Schedule> fetch = queryFactory
			.select(schedule)
			.from(schedule)
			.leftJoin(schedule.trainer, trainer).fetchJoin()
			.leftJoin(schedule.scheduleWaiting, scheduleWaiting).fetchJoin()
			.where(scheduleApplicantIdEq(memberId))
			.orderBy(schedule.lessonDt.desc(), schedule.lessonStartTime.asc())
			.fetch();
		return fetch.stream()
			.map(ScheduleCommandResult::from)
			.collect(toList());
	}

	@Override
	public List<MyReservation> findNewReservation(Long memberId, StudentScheduleCond searchCond) {
		List<Schedule> schedules = queryFactory.select(schedule)
				.from(schedule)
				.innerJoin(schedule.applicant, new QMember("applicant")).fetchJoin()
				.innerJoin(schedule.trainer, new QMember("trainer")).fetchJoin()
				.where(scheduleApplicantIdEq(memberId), lessonDateTimeAfterNow(), lessonDtEq(searchCond), courseIdEq(searchCond))
				.orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
				.fetch();
		return schedules.stream().map(MyReservation::from).collect(toList());
	}

	@Override
	public List<MyReservation> findOldReservation(Long memberId, StudentScheduleCond searchCond, String searchDate) {
		List<Schedule> schedules = queryFactory.select(schedule)
				.from(schedule)
				.innerJoin(schedule.applicant, new QMember("applicant")).fetchJoin()
				.innerJoin(schedule.trainer, new QMember("trainer")).fetchJoin()
				.where(scheduleApplicantIdEq(memberId), lessonDateTimeBeforeNow(), lessonDtEq(searchCond)
						, courseIdEq(searchCond), convertDateFormat(searchDate))
				.orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
				.fetch();
		return schedules.stream().map(MyReservation::from).collect(toList());
	}

	@Override
	public MyReservation findMyNextReservation(Long memberId) {
		Schedule result = queryFactory.select(schedule)
				.from(schedule)
				.where(scheduleApplicantIdEq(memberId), lessonDateTimeAfterNow())
				.orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
				.limit(1)
				.fetchOne();
		return result==null ? null : MyReservation.from(result);
	}

	private BooleanExpression scheduleReservationStatusForStudent() {
		return schedule.reservationStatus.eq(COMPLETED)
				.or(schedule.reservationStatus.eq(AVAILABLE))
				.or(schedule.reservationStatus.eq(SOLD_OUT));
	}

	private Predicate lessonDateTimeAfterNow() {
		return schedule.lessonDt.after(LocalDate.now())
				.or(schedule.lessonDt.goe(LocalDate.now()).and(schedule.lessonStartTime.after(LocalTime.now())));
	}

	private Predicate lessonDateTimeBeforeNow() {
		return schedule.lessonDt.before(LocalDate.now())
				.or(schedule.lessonDt.loe(LocalDate.now()).and(schedule.lessonStartTime.before(LocalTime.now())));
	}

	private BooleanExpression lessonDtBetween(StudentScheduleCond searchCond) {
		if (!ObjectUtils.isEmpty(searchCond.getLessonStartDt()) && !ObjectUtils.isEmpty(searchCond.getLessonEndDt())) {
			return schedule.lessonDt.between(searchCond.getLessonStartDt(), searchCond.getLessonEndDt());
		}
		return null;
	}

	private BooleanExpression lessonDtEq(StudentScheduleCond searchCond) {
		if (!ObjectUtils.isEmpty(searchCond.getLessonDt())) {
			StringExpression formattedDate = stringTemplate("DATE_FORMAT({0}, '%Y%m')", schedule.lessonDt);
			return formattedDate.eq(searchCond.getLessonDt());
		}
		return null;
	}

	private BooleanExpression scheduleTrainerIdEq(Long trainerId) {
		if(!ObjectUtils.isEmpty(trainerId)){
			return schedule.trainer.id.eq(trainerId);
		}
		return null;
	}

	private BooleanExpression scheduleApplicantIdEq(Long memberId) {
		if(!ObjectUtils.isEmpty(memberId)){
			return schedule.applicant.id.eq(memberId);
		}
		return null;
	}

	private Predicate courseIdEq(StudentScheduleCond searchCond) {
		if (!ObjectUtils.isEmpty(searchCond.getCourseId())) {
			return schedule.course.courseId.eq(searchCond.getCourseId());
		}
		return null;
	}

	private BooleanExpression convertDateFormat(String searchDate) {
		if (!ObjectUtils.isEmpty(searchDate)){
			StringTemplate stringTemplate = Expressions.stringTemplate(
					"DATE_FORMAT({0}, {1})"
					, schedule.lessonDt
					, ConstantImpl.create("%Y-%m"));
			return stringTemplate.eq(searchDate);
		}
		return null;
	}
}

package com.tobe.healthy.schedule.repository.trainer;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbySchedule;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;
import static com.tobe.healthy.schedule.domain.entity.QStandBySchedule.standBySchedule;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TrainerScheduleRepositoryImpl implements TrainerScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Long trainerId, Member member) {
		List<Schedule> results = queryFactory
				.select(schedule)
				.from(schedule)
				.leftJoin(schedule.trainer, new QMember("trainer")).fetchJoin()
				.leftJoin(schedule.applicant, new QMember("applicant")).fetchJoin()
				.leftJoin(schedule.standBySchedule, standBySchedule).on(standBySchedule.delYn.isFalse())
				.where(lessonDtEq(searchCond), lessonDtBetween(searchCond), delYnFalse(), schedule.trainer.id.eq(trainerId))
				.orderBy(schedule.lessonDt.asc(), schedule.lessonStartTime.asc())
				.fetch();

		return results.stream()
				.map(result -> ScheduleCommandResult.from(result, member))
				.collect(toList());
	}

	private BooleanExpression delYnFalse() {
		return schedule.delYn.eq(false);
	}

	@Override
	public List<MyStandbySchedule> findAllMyStandbySchedule(Long memberId) {
		List<StandBySchedule> results = queryFactory.select(standBySchedule)
				.from(standBySchedule)
				.innerJoin(standBySchedule.schedule, schedule).fetchJoin()
				.innerJoin(standBySchedule.member, new QMember("member")).fetchJoin()
				.innerJoin(schedule.trainer, new QMember("trainer")).fetchJoin()
				.where(standBySchedule.member.id.eq(memberId), standBySchedule.delYn.isFalse(),
						standBySchedule.schedule.lessonDt.goe(LocalDate.now()))
				.orderBy(standBySchedule.schedule.lessonDt.asc(), standBySchedule.schedule.lessonStartTime.asc())
				.fetch();
		return results.stream().map(MyStandbySchedule::from).collect(toList());
	}

	@Override
	public Optional<Schedule> findAvailableRegisterSchedule(RegisterScheduleCommand request, Long trainerId) {
		Schedule entity = queryFactory
				.select(schedule)
				.from(schedule)
				.where(
						schedule.lessonDt.eq(request.getLessonDt()),
						schedule.lessonStartTime.between(request.getLessonStartTime(), request.getLessonEndTime()),
						schedule.lessonEndTime.between(request.getLessonStartTime(), request.getLessonEndTime()),
						schedule.trainer.id.eq(trainerId)
				)
				.fetchOne();
		return Optional.ofNullable(entity);
	}

	@Override
	public Boolean validateRegisterSchedule(LocalDate lessonDt, LocalTime startTime, LocalTime endTime, Long trainerId) {
		return queryFactory.select(schedule.count().gt(0).as("isScheduleRegisterd"))
				.from(schedule)
				.where(
						schedule.lessonDt.eq(lessonDt),
						schedule.trainer.id.eq(trainerId),
						(schedule.lessonStartTime.between(startTime, endTime).or(schedule.lessonEndTime.between(startTime, endTime)))
				)
				.fetchOne();
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

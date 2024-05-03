package com.tobe.healthy.schedule.repository.schedule_waiting;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.schedule.domain.dto.out.MyScheduleWaiting;
import com.tobe.healthy.schedule.domain.entity.ScheduleWaiting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;
import static com.tobe.healthy.schedule.domain.entity.QScheduleWaiting.scheduleWaiting;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ScheduleWaitingRepositoryImpl implements ScheduleWaitingRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<MyScheduleWaiting> findAllMyScheduleWaiting(Long memberId) {
		List<ScheduleWaiting> results = queryFactory.select(scheduleWaiting)
				.from(scheduleWaiting)
				.innerJoin(scheduleWaiting.schedule, schedule).fetchJoin()
				.innerJoin(scheduleWaiting.member, new QMember("member")).fetchJoin()
				.innerJoin(schedule.trainer, new QMember("trainer")).fetchJoin()
				.where(scheduleWaiting.member.id.eq(memberId), scheduleWaiting.delYn.isFalse(),
						scheduleWaiting.schedule.lessonDt.goe(LocalDate.now()))
				.orderBy(scheduleWaiting.schedule.lessonDt.asc(), scheduleWaiting.schedule.lessonStartTime.asc())
				.fetch();
		return results.stream().map(MyScheduleWaiting::from).collect(toList());
	}

}

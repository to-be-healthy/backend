package com.tobe.healthy.schedule.repository.student;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.QMember;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbySchedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.tobe.healthy.schedule.domain.entity.QSchedule.schedule;
import static com.tobe.healthy.schedule.domain.entity.QStandBySchedule.standBySchedule;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StandByScheduleRepositoryImpl implements StandByScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;

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

}

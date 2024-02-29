package com.tobe.healthy.schedule.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.dto.ScheduleCommand;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final MemberRepository memberRepository;

	@Override
	public LocalDateTime registerSchedule(ScheduleCommand request) {
		Schedule.builder()
			.startDate(request.getStartDate())
			.isReserve("false")
			.round(request.getRound())
			.trainerId(findByEmail(request.getTrainerEmail()))
			.applicantId(findByEmail(request.getApplicantEmail()))
			.build();
		return request.getStartDate();
	}

	private Member findByEmail(String email) {
		return memberRepository.findByEmail(email).orElse(null);
	}
}

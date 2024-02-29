package com.tobe.healthy.schedule;

import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static com.tobe.healthy.member.domain.entity.MemberCategory.MEMBER;
import static com.tobe.healthy.member.domain.entity.MemberCategory.TRAINER;
import static org.assertj.core.api.Assertions.assertThat;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.application.ScheduleService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
public class ScheduleTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private ScheduleService scheduleService;

	@BeforeEach
	@Transactional
	void registerSchedule() {
		Member member = Member.builder()
			.email("laborlawseon@gmail.com")
			.password("12345678")
			.nickname("seonwoo_jung")
			.isAlarm(ABLE)
			.category(MEMBER)
			.mobileNum("010-1234-1234")
			.build();

		Member trainer = Member.builder()
			.email("laborlawseon2@gmail.com")
			.password("123456789")
			.nickname("seonwoo_jung2")
			.isAlarm(ABLE)
			.category(TRAINER)
			.mobileNum("010-4321-4321")
			.build();

		// given
		Schedule schedule = Schedule.builder()
			.startDate(LocalDateTime.of(2024, 2, 29, 10, 0))
			.isReserve("true")
			.round("1")
			.trainerId(trainer)
			.applicantId(member)
			.build();

		em.persist(schedule);
	}

	@Test
	@DisplayName("모든 일정을 확인한다.")
	void findSchedule() {
	    // given
		List<Schedule> schedules = scheduleService.findAllSchedule();
		for (Schedule schedule : schedules) {
			log.info("schedule => {}", schedule);
		}
	}

	@Test
	@DisplayName("일정을 취소한다.")
	@Rollback(false)
	void cancelSchedule() {
	    // given
		LocalDateTime result = scheduleService.modifySchedule(LocalDateTime.of(2024, 2, 29, 10, 0));
	    // then
	    assertThat(result).isEqualTo(LocalDateTime.of(2024, 2, 29, 10, 0));
	}
}
package com.tobe.healthy.schedule.application;

import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static com.tobe.healthy.member.domain.entity.MemberCategory.MEMBER;
import static com.tobe.healthy.member.domain.entity.MemberCategory.TRAINER;
import static java.time.LocalDateTime.of;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest.ScheduleRegisterInfo;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
@DisplayName("일정 기능 테스트")
class ScheduleServiceTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private ScheduleService scheduleService;

	@Nested
	@DisplayName("일정 등록")
	class CreateSchedule {

		@Test
		@DisplayName("임시 일정을 생성한다.")
		void autoCreateSchedule() {
			AutoCreateScheduleCommandRequest request = AutoCreateScheduleCommandRequest.builder()
				.trainer(1L)
				.startDt(of(2024, 3, 4, 10, 0))
				.endDt(of(2024, 3, 4, 22, 0))
				.lessonTime(50)
				.breakTime(10)
				.build();
			List<ScheduleCommandResponse> lists = scheduleService.autoCreateSchedule(request);
			for (ScheduleCommandResponse list : lists) {
				log.info("list => {}", list);
			}
		}

		@Test
		@DisplayName("일정을 등록한다.")
		void registerSchedule() {
			Member member = Member.builder()
				.email("member@gmail.com")
				.password("12345678")
				.nickname("member")
				.isAlarm(ABLE)
				.category(MEMBER)
				.build();

			Member trainer = Member.builder()
				.email("trainer@gmail.com")
				.password("12345678")
				.nickname("trainer")
				.isAlarm(ABLE)
				.category(TRAINER)
				.build();

			em.persist(trainer);

			AutoCreateScheduleCommandRequest request = AutoCreateScheduleCommandRequest.builder()
				.trainer(1L)
				.startDt(of(2024, 3, 4, 10, 0))
				.endDt(of(2024, 3, 4, 22, 0))
				.lessonTime(50)
				.breakTime(10)
				.build();
			List<ScheduleCommandResponse> lists = scheduleService.autoCreateSchedule(request);

			List<ScheduleRegisterInfo> requests = new ArrayList<>();
			for (ScheduleCommandResponse list : lists) {
				requests.add(new ScheduleRegisterInfo(list.getRound(), list.getStartDt(), list.getEndDt(), member.getId()));
			}

			ScheduleCommandRequest param = ScheduleCommandRequest.builder()
				.trainer(trainer.getId())
				.list(requests)
				.build();

			Boolean result = scheduleService.registerSchedule(param);
			log.info("result => {}", result);
		}
	}

	@Nested
	@DisplayName("일정을 조회")
	class FindSchedule {

		@Test
		@DisplayName("전체 일정을 조회한다.")
		void findAllSchedule() {
			List<ScheduleCommandResult> lists = scheduleService.findAllSchedule();
			for (ScheduleCommandResult list : lists) {
				log.info("list => {}", list);
			}
		}
	}

	@Nested
	@DisplayName("일정 취소")
	class CancelSchedule {

		@Test
		@DisplayName("일정을 취소한다.")
		void cancelSchedule() {

		}
	}
}
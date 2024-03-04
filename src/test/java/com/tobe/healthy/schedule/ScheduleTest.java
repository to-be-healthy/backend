package com.tobe.healthy.schedule;

import static com.tobe.healthy.member.domain.entity.Alarm.ABLE;
import static com.tobe.healthy.member.domain.entity.MemberCategory.MEMBER;
import static com.tobe.healthy.member.domain.entity.MemberCategory.TRAINER;
import static java.time.LocalDateTime.of;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.application.ScheduleCommandResponse;
import com.tobe.healthy.schedule.application.ScheduleService;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest.ScheduleRegisterInfo;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
public class ScheduleTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private ScheduleService scheduleService;

	@Test
	@DisplayName("자동으로 임시 일정을 생성한다.")
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

	@Test
	void findAllSchedule() {
		registerSchedule();
		List<ScheduleCommandResult> lists = scheduleService.findAllSchedule();
		for (ScheduleCommandResult list : lists) {
			log.info("list => {}", list);
		}
	}

	private static int calculateRound(AutoCreateScheduleCommandRequest request) {
		Duration duration = Duration.between(request.getStartDt(), request.getEndDt());
		long hours = duration.toMinutes();
		int round = (int) hours / (request.getBreakTime() + request.getLessonTime());
		return round;
	}

	@Test
	@DisplayName("일정을 취소한다.")
	void cancelSchedule() {
	}
}
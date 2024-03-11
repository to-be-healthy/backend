//package com.tobe.healthy.schedule.application;
//
//import static com.tobe.healthy.member.domain.entity.AlarmStatus.ENABLED;
//import static com.tobe.healthy.member.domain.entity.MemberType.MEMBER;
//import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;
//import static java.time.LocalDateTime.of;
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.tobe.healthy.member.domain.entity.Member;
//import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommandRequest;
//import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest;
//import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest.ScheduleRegisterInfo;
//import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResponse;
//import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
//import jakarta.persistence.EntityManager;
//import java.util.ArrayList;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Slf4j
//@Transactional
//@DisplayName("일정 기능 테스트")
//class ScheduleServiceTest {
//
//	@Autowired
//	private EntityManager em;
//
//	@Autowired
//	private ScheduleService scheduleService;
//
//	@Nested
//	@DisplayName("일정 등록")
//	class CreateSchedule {
//
//		@Test
//		@DisplayName("임시 일정을 생성한다.")
//		void autoCreateSchedule() {
//			AutoCreateScheduleCommandRequest request = AutoCreateScheduleCommandRequest.builder()
//				.trainer(1L)
//				.startDt(of(2024, 3, 4, 10, 0))
//				.endDt(of(2024, 3, 4, 22, 0))
//				.lessonTime(50)
//				.breakTime(10)
//				.build();
//
//			List<ScheduleCommandResponse> lists = scheduleService.autoCreateSchedule(request);
//
//			assertThat(lists.size()).isEqualTo(12);
//		}
//	}
//
//	@Nested
//	@DisplayName("일정을 조회")
//	class FindSchedule {
//
//		@Test
//		@DisplayName("전체 일정을 조회한다.")
//		void findAllSchedule() {
//			List<ScheduleCommandResult> lists = scheduleService.findAllSchedule();
//			for (ScheduleCommandResult list : lists) {
//				log.info("list => {}", list);
//			}
//		}
//	}
//
//	@Nested
//	@DisplayName("일정 취소")
//	class CancelSchedule {
//
//		@Test
//		@DisplayName("일정을 취소한다.")
//		void cancelSchedule() {
//
//		}
//	}
//}
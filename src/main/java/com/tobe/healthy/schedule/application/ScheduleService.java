package com.tobe.healthy.schedule.application;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.SCHEDULE_NOT_FOUND;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest.ScheduleRegisterInfo;
import com.tobe.healthy.schedule.repository.ScheduleRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleService {

	private final MemberRepository memberRepository;

	private final ScheduleRepository scheduleRepository;

	public List<ScheduleCommandResponse> autoCreateSchedule(AutoCreateScheduleCommandRequest request) {
		int round = calculateRound(request);
		List<ScheduleCommandResponse> list = new ArrayList<>();
		LocalDateTime curDt = request.getStartDt();

		// 수업 시작 시간 + 수업 종료 시간 + 수업당 시간 + 휴식 시간
		for (int i = 1; i <= round; i++) {
			list.add(new ScheduleCommandResponse(i, curDt, curDt.plusMinutes(request.getLessonTime())));
			curDt = curDt.plusMinutes(request.getBreakTime()).plusMinutes(request.getLessonTime());
		}
		return list;
	}

	public Boolean registerSchedule(ScheduleCommandRequest request) {
		Member trainer = memberRepository.findById(request.getTrainer())
				.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		for (ScheduleRegisterInfo list : request.getList()) {
			Member member = null;
			if (!ObjectUtils.isEmpty(list.getApplicant())) {
				member = memberRepository.findById(list.getApplicant())
					.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
			}
			Schedule schedule = Schedule.registerSchedule(trainer, member, list);
			scheduleRepository.save(schedule);
		}

		return true;
	}

	public List<ScheduleCommandResult> findAllSchedule() {
		return scheduleRepository.findAllSchedule();
	}

	public Boolean cancelSchedule(Long scheduleId) {
		Schedule entity = scheduleRepository.findById(scheduleId)
					.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));
		entity.cancelSchedule();
		return true;
	}

	private int calculateRound(AutoCreateScheduleCommandRequest request) {
		Duration duration = Duration.between(request.getStartDt(), request.getEndDt());
		long hours = duration.toMinutes();
		int round = (int) hours / (request.getBreakTime() + request.getLessonTime());
		return round;
	}
}

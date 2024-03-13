package com.tobe.healthy.schedule.application;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.NOT_STAND_BY_SCHEDULE;
import static com.tobe.healthy.config.error.ErrorCode.NOT_RESERVABLE_SCHEDULE;
import static com.tobe.healthy.config.error.ErrorCode.SCHEDULE_NOT_FOUND;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.stream.Collectors.toList;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleCommandRequest.ScheduleRegister;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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

	private final StandByScheduleRepository standByScheduleRepository;

	public TreeMap<LocalDate, ArrayList<ScheduleInfo>> autoCreateSchedule(AutoCreateScheduleCommandRequest request) {
		TreeMap<LocalDate, ArrayList<ScheduleInfo>> map = new TreeMap<>();
		LocalDate startDt = request.getStartDt();
		while (!startDt.isAfter(request.getEndDt())) {
			ArrayList<ScheduleInfo> scheduleInfos = new ArrayList<>();
			int round = 1;
			if (isHoliday(startDt)) {
				// 주말일경우
				LocalTime startTime = request.getWeekendStartTime();
				while (!startTime.isAfter(request.getWeekendEndTime())) {
					scheduleInfos.add(new ScheduleInfo(startTime, startTime.plusMinutes(request.getLessonTime()), round++));
					startTime = startTime.plusMinutes(request.getLessonTime() + request.getBreakTime());
				}
			} else {
				LocalTime startTime = request.getWeekdayStartTime();
				while (!startTime.isAfter(request.getWeekdayEndTime())) {
					scheduleInfos.add(new ScheduleInfo(startTime, startTime.plusMinutes(request.getLessonTime()), round++));
					startTime = startTime.plusMinutes(request.getLessonTime() + request.getBreakTime());
				}
			}
			map.put(startDt, scheduleInfos);
			startDt = startDt.plusDays(1);
		}
		return map;
	}

	public List<ScheduleCommandResult> findAllByApplicantId(Long memberId) {
		List<Schedule> schedules = scheduleRepository.findAllByApplicantId(memberId);
		return schedules.stream()
			.map(ScheduleCommandResult::of)
			.collect(toList());
	}

	public Boolean reserveSchedule(Long scheduleId) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		Member member = memberRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Schedule schedule = scheduleRepository.findAvailableScheduleById(scheduleId)
			.orElseThrow(() -> new CustomException(NOT_RESERVABLE_SCHEDULE));

		schedule.registerSchedule(member);

		return true;
	}

	public Boolean registerStandBySchedule(Long scheduleId) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		Member member = memberRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Schedule schedule = scheduleRepository.findAvailableStandById(scheduleId)
			.orElseThrow(() -> new CustomException(NOT_STAND_BY_SCHEDULE));

		StandBySchedule standBySchedule = StandBySchedule.register(member, schedule);

		schedule.registerSchedule(standBySchedule);

		return true;
	}

	@Data
	@AllArgsConstructor
	public static class ScheduleInfo {
		LocalTime startTime;
		LocalTime endTime;
		int round;
	}

	private boolean isHoliday(LocalDate startDt) {
		return startDt.getDayOfWeek().equals(SUNDAY) || startDt.getDayOfWeek().equals(SATURDAY);
	}

	public Boolean registerSchedule(ScheduleCommandRequest request) {
		for (Map.Entry<String, List<ScheduleRegister>> entry : request.getSchedule().entrySet()) {
			String date = entry.getKey();
			List<ScheduleRegister> scheduleRegisters = entry.getValue();
			Member trainer = memberRepository.findById(request.getTrainer())
				.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
			for (ScheduleRegister scheduleRegister : scheduleRegisters) {
				Member applicant = null;
				if (!ObjectUtils.isEmpty(scheduleRegister.getApplicant())) {
					applicant = memberRepository.findById(scheduleRegister.getApplicant())
						.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
				}
				Schedule entity = Schedule.registerSchedule(LocalDate.parse(date), trainer, applicant, scheduleRegister);
				scheduleRepository.save(entity);
			}
		}
		return true;
	}

	public List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond) {
		return scheduleRepository.findAllSchedule(searchCond);
	}

	public Boolean cancelTrainerSchedule(Long scheduleId) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		Member trainer = memberRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Schedule entity = scheduleRepository.findByTrainerIdAndId(trainer.getId(), scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		entity.cancelSchedule();
		return true;
	}

	public Boolean cancelMemberSchedule(Long scheduleId) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		Schedule entity = scheduleRepository.findById(scheduleId)
				.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		entity.cancelSchedule();
		return true;
	}
}

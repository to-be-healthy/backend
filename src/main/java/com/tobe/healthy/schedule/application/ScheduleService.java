package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.RegisterClosedDayCommand;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservationResponse;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbyScheduleResponse;
import com.tobe.healthy.schedule.domain.dto.out.NoShowCommandResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleService {

	private final MemberRepository memberRepository;

	private final ScheduleRepository scheduleRepository;

	private final StandByScheduleRepository standByScheduleRepository;

	public Boolean registerSchedule(AutoCreateScheduleCommand request, Long trainerId) {
		validateSchduleDate(request);

		Member trainer = memberRepository.findById(trainerId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		LocalDate startDt = request.getStartDt();
		while (!startDt.isAfter(request.getEndDt())) {
			int round = 1;

            LocalTime startTime = request.getStartTime();
            while (!startTime.isAfter(request.getEndTime())) {
				if (request.getClosedDt() != null && request.getClosedDt().equals(startDt)) {
					Schedule entity = Schedule.builder().reservationStatus(CLOSED_DAY).build();
					scheduleRepository.save(entity);
					startDt = startDt.plusDays(1);
					continue;
				}
                if (startTime.equals(request.getLunchStartTime())) {
                    Duration duration = Duration.between(request.getLunchStartTime(), request.getLunchEndTime());
                    startTime = startTime.plusMinutes(duration.toMinutes());
					Schedule entity = Schedule.registerSchedule(startDt, trainer, startTime, startTime.plusMinutes(request.getSessionTime().getDescription()), 0, LUNCH_TIME);
					scheduleRepository.save(entity);
                    continue;
                }
                startTime = startTime.plusMinutes(request.getSessionTime().getDescription());
				if (startTime.equals(request.getEndTime())) {
					break;
				}
				Schedule entity = Schedule.registerSchedule(startDt, trainer, startTime, startTime.plusMinutes(request.getSessionTime().getDescription()), round++, AVAILABLE);
				scheduleRepository.save(entity);
			}
			startDt = startDt.plusDays(1);
		}
		return true;
	}

	public Boolean registerIndividualSchedule(RegisterScheduleCommand request, Long trainerId) {
		scheduleRepository.findAvailableRegisterSchedule(request, trainerId).ifPresentOrElse(
				schedule -> {
					throw new CustomException(SCHEDULE_ALREADY_EXISTS);
				},
				() -> {
					Member trainer = memberRepository.findById(trainerId)
							.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
					Schedule entity = Schedule.registerSchedule(request.getLessonDt(), trainer, request.getLessonStartTime(), request.getLessonEndTime(), 0, AVAILABLE);
					scheduleRepository.save(entity);
				}
		);
		return true;
	}

	public Boolean registerClosedDay(RegisterClosedDayCommand request, Long trainerId) {
		Member trainer = memberRepository.findById(trainerId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		request.getLessonDt().stream().forEach(lessonDt -> scheduleRepository.findScheduleByLessonDt(lessonDt).ifPresentOrElse(
                schedule -> {
                    throw new CustomException(SCHEDULE_ALREADY_EXISTS);
                },
                () -> {
                    Schedule entity = Schedule.builder().lessonDt(lessonDt).trainer(trainer).reservationStatus(CLOSED_DAY).build();
                    scheduleRepository.save(entity);
                }
        ));
		return true;
	}

	public List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Long trainerId) {
		return scheduleRepository.findAllSchedule(searchCond, trainerId);
	}

	public List<ScheduleCommandResult> findAllByApplicantId(Long memberId) {
		return scheduleRepository.findAllByApplicantId(memberId);
	}

	public Boolean reserveSchedule(Long scheduleId, Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Schedule schedule = scheduleRepository.findAvailableScheduleById(scheduleId)
				.orElseThrow(() -> new CustomException(NOT_RESERVABLE_SCHEDULE));

		schedule.registerSchedule(member);

		return true;
	}

	public Boolean cancelMemberSchedule(Long scheduleId, Long memberId) {
		Schedule schedule = scheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
				.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		// 대기 테이블에 인원이 있으면 수정하기
		standByScheduleRepository.findByScheduleId(scheduleId).ifPresentOrElse(
				standBySchedule -> changeApplicantAndDeleteStandBy(standBySchedule, schedule),
				() -> schedule.cancelMemberSchedule()
		);

		return true;
	}

	public Boolean cancelTrainerSchedule(Long scheduleId, Long memberId) {
		Schedule entity = scheduleRepository.findScheduleByTrainerId(memberId, scheduleId)
				.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		entity.cancelTrainerSchedule();

		return true;
	}

	public List<MyReservationResponse> findAllMyReservation(Long memberId) {
		return scheduleRepository.findAllMyReservation(memberId);
	}

	public List<MyStandbyScheduleResponse> findAllMyStandbySchedule(Long memberId) {
		return scheduleRepository.findAllMyStandbySchedule(memberId);
	}

	public NoShowCommandResponse updateReservationStatusToNoShow(Long scheduleId, Long memberId) {
		Schedule schedule = scheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
				.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));
		schedule.updateReservationStatusToNoShow();
		return NoShowCommandResponse.from(schedule);
	}

	private void validateSchduleDate(AutoCreateScheduleCommand request) {
		if (request.getStartDt().isAfter(request.getEndDt())) {
			throw new CustomException(START_DATE_AFTER_END_DATE);
		}
		if (request.getStartTime().isAfter(request.getEndTime())) {
			throw new CustomException(DATETIME_NOT_VALID);
		}
		if (request.getLunchStartTime().isAfter(request.getLunchEndTime())) {
			throw new CustomException(LUNCH_TIME_INVALID);
		}
		if (ChronoUnit.DAYS.between(request.getStartDt(), request.getEndDt()) > 30) {
			throw new CustomException(SCHEDULE_LESS_THAN_30_DAYS);
		}
	}

	private void changeApplicantAndDeleteStandBy(StandBySchedule standBySchedule, Schedule schedule) {
		schedule.changeApplicantInSchedule(standBySchedule.getMember());
		standBySchedule.deleteStandBy();
	}
}

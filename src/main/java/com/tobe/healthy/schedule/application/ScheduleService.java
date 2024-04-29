package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.RegisterClosedDayCommand;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.*;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.ScheduleRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.course.domain.entity.CourseHistoryType.RESERVATION;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;
import static com.tobe.healthy.member.domain.entity.MemberType.TRAINER;
import static com.tobe.healthy.point.domain.entity.Calculation.MINUS;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.*;
import static java.time.LocalTime.NOON;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleService {

	private final MemberRepository memberRepository;
	private final ScheduleRepository scheduleRepository;
	private final StandByScheduleRepository standByScheduleRepository;
	private final CourseRepository courseRepository;
	private final CourseService courseService;
	private final TrainerMemberMappingRepository mappingRepository;

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

	public List<ScheduleCommandResult> findAllByApplicantId(Long memberId) {
		return scheduleRepository.findAllByApplicantId(memberId);
	}

	public Boolean reserveSchedule(Long scheduleId, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Course course = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, 0)
				.orElseThrow(() -> new CustomException(LESSON_CNT_NOT_VALID));

		Schedule schedule = scheduleRepository.findAvailableScheduleById(scheduleId)
			.orElseThrow(() -> new CustomException(NOT_RESERVABLE_SCHEDULE));

		schedule.registerSchedule(member);
		CourseUpdateCommand command = CourseUpdateCommand.create(memberId, MINUS, RESERVATION, 1);
		courseService.updateCourse(schedule.getTrainer().getId(), course.getCourseId(), command);
		return true;
	}

	public ScheduleCommandResponse findAllSchedule(ScheduleSearchCond searchCond, Member loginMember) {
		Long trainerId;
		if(STUDENT.equals(loginMember.getMemberType())){
			TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(loginMember.getId())
					.orElseThrow(() -> new CustomException(TRAINER_NOT_MAPPED));
			trainerId = mapping.getTrainer().getId();
		}else{
			trainerId = loginMember.getId();
		}
		List<ScheduleCommandResult> schedule = scheduleRepository.findAllSchedule(searchCond, trainerId);

		List<ScheduleCommandResult> morning = schedule.stream()
				.filter(s -> NOON.isAfter(s.getLessonStartTime())).collect(Collectors.toList());
		List<ScheduleCommandResult> afternoon = schedule.stream()
				.filter(s -> NOON.isBefore(s.getLessonStartTime())).collect(Collectors.toList());
		return ScheduleCommandResponse.create(morning, afternoon);
	}

	public Boolean cancelTrainerSchedule(Long scheduleId, Long memberId) {
		Schedule entity = scheduleRepository.findScheduleByTrainerId(memberId, scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		entity.cancelTrainerSchedule();

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

	public List<MyReservationResponse> findAllMyReservation(Long memberId, ScheduleSearchCond searchCond) {
		return scheduleRepository.findAllMyReservation(memberId, searchCond);
	}

	public List<MyStandbyScheduleResponse> findAllMyStandbySchedule(Long memberId) {
		return scheduleRepository.findAllMyStandbySchedule(memberId);
	}

	public NoShowCommandResponse updateReservationStatusToNoShow(Long scheduleId, Long memberId) {
		Schedule schedule = scheduleRepository.findScheduleByTrainerId(memberId, scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));
		schedule.updateReservationStatusToNoShow();
		return NoShowCommandResponse.from(schedule);
	}

	public Boolean registerIndividualSchedule(RegisterScheduleCommand request, Long trainerId) {
		scheduleRepository.findAvailableRegisterSchedule(request, trainerId).ifPresentOrElse(
				schedule -> {
					Member trainer = memberRepository.findById(trainerId)
							.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
					Schedule entity = Schedule.registerSchedule(request.getLessonDt(), trainer, request.getLessonStartTime(), request.getLessonEndTime(), 0, AVAILABLE);
					scheduleRepository.save(entity);
				},
				() -> {
					throw new CustomException(SCHEDULE_ALREADY_EXISTS);
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

	private void changeApplicantAndDeleteStandBy(StandBySchedule standBySchedule, Schedule schedule) {
		schedule.changeApplicantInSchedule(standBySchedule.getMember());
		standBySchedule.deleteStandBy();
	}
}

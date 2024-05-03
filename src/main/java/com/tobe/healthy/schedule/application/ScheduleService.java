package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommand;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.AVAILABLE;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.SOLD_OUT;
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
	private final TrainerMemberMappingRepository mappingRepository;

	public Boolean registerSchedule(AutoCreateScheduleCommand request, Long trainerId) {
		validateScheduleDate(request);

		Member trainer = memberRepository.findById(trainerId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		LocalDate startDt = request.getStartDt();
		while (!startDt.isAfter(request.getEndDt())) {
            LocalTime startTime = request.getStartTime();
            while (!startTime.isAfter(request.getEndTime())) {
				if (isEqualsClosedDt(request, startDt)) {
					startDt = startDt.plusDays(1);
					continue;
				}
                if (isEqualsLunchTime(request, startTime)) {
                    Duration duration = Duration.between(request.getLunchStartTime(), request.getLunchEndTime());
                    startTime = startTime.plusMinutes(duration.toMinutes());
                    continue;
                }
                startTime = startTime.plusMinutes(request.getSessionTime().getDescription());
				if (startDtIsEqualsEndDt(request, startTime)) {
					break;
				}
				Schedule schedule =
					Schedule.registerSchedule(
						startDt,
						trainer,
						startTime,
						startTime.plusMinutes(request.getSessionTime().getDescription()),
						AVAILABLE
					);
				scheduleRepository.save(schedule);
			}
			startDt = startDt.plusDays(1);
		}
		return true;
	}

	private boolean startDtIsEqualsEndDt(AutoCreateScheduleCommand request, LocalTime startTime) {
		return startTime.equals(request.getEndTime());
	}

	private boolean isEqualsLunchTime(AutoCreateScheduleCommand request, LocalTime startTime) {
		return startTime.equals(request.getLunchStartTime());
	}

	private boolean isEqualsClosedDt(AutoCreateScheduleCommand request, LocalDate startDt) {
		return request.getClosedDt() != null && request.getClosedDt().equals(startDt);
	}

	private void validateScheduleDate(AutoCreateScheduleCommand request) {
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
		List<ScheduleCommandResult> result = scheduleRepository.findAllByApplicantId(memberId);
		return result.isEmpty() ? null : result;
	}

	public ScheduleIdInfo reserveSchedule(Long scheduleId, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Schedule schedule = scheduleRepository.findAvailableScheduleById(scheduleId)
			.orElseThrow(() -> new CustomException(NOT_RESERVABLE_SCHEDULE));

		LocalTime before24Hour = schedule.getLessonStartTime().minusMinutes(30);
		if(LocalTime.now().isAfter(before24Hour)) throw new CustomException(RESERVATION_NOT_VALID);

		schedule.registerSchedule(member);
		return ScheduleIdInfo.from(schedule);
	}

	public List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Member trainer) {
		return scheduleRepository.findAllSchedule(searchCond, trainer.getId(), trainer);
	}

	public ScheduleCommandResponse findAllScheduleOfTrainer(ScheduleSearchCond searchCond, Member member) {

		TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(member.getId())
				.orElseThrow(() -> new CustomException(TRAINER_NOT_MAPPED));

		Long trainerId = mapping.getTrainer().getId();

		List<ScheduleCommandResult> schedule = scheduleRepository.findAllSchedule(searchCond, trainerId, member);

		List<ScheduleCommandResult> morning = schedule.stream()
				.filter(s -> NOON.isAfter(s.getLessonStartTime()))
				.peek(s -> { if(s.getStandByName()!=null) s.setReservationStatus(SOLD_OUT); })
				.collect(Collectors.toList());

		List<ScheduleCommandResult> afternoon = schedule.stream()
				.filter(s -> NOON.isBefore(s.getLessonStartTime()))
				.peek(s -> { if(s.getStandByName()!=null) s.setReservationStatus(SOLD_OUT); })
				.collect(Collectors.toList());

		return ScheduleCommandResponse.create(morning, afternoon);
	}

	public Boolean cancelTrainerSchedule(Long scheduleId, Long memberId) {
		Schedule entity = scheduleRepository.findScheduleByTrainerId(memberId, scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		entity.cancelTrainerSchedule();

		return true;
	}

	public ScheduleIdInfo cancelMemberSchedule(Long scheduleId, Long memberId) {
		Schedule schedule = scheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.now());
		LocalDateTime before24Hour = LocalDateTime.of(schedule.getLessonDt().minusDays(1), schedule.getLessonStartTime());
		if(now.isAfter(before24Hour)) throw new CustomException(RESERVATION_CANCEL_NOT_VALID);

		// 대기 테이블에 인원이 있으면 수정하기
		Optional<StandBySchedule> standByScheduleOpt = standByScheduleRepository.findByScheduleId(scheduleId);
		if(standByScheduleOpt.isPresent()){
			StandBySchedule standBySchedule = standByScheduleOpt.get();
			changeApplicantAndDeleteStandBy(standBySchedule, schedule);
			return ScheduleIdInfo.create(memberId, schedule, standBySchedule.getMember().getId());
		}else{
			ScheduleIdInfo idInfo = ScheduleIdInfo.from(schedule);
			schedule.cancelMemberSchedule();
			return idInfo;
		}
	}

	public MyReservationResponse findAllMyReservation(Long memberId, ScheduleSearchCond searchCond) {
		Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, -1);
		CourseDto course = optCourse.map(CourseDto::from).orElse(null);
		List<MyReservation> result = scheduleRepository.findAllMyReservation(memberId, searchCond);
		return MyReservationResponse.create(course, result);
	}

	public MyStandbyScheduleResponse findAllMyStandbySchedule(Long memberId) {
		Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, -1);
		CourseDto course = optCourse.map(CourseDto::from).orElse(null);
		List<MyStandbySchedule> result = scheduleRepository.findAllMyStandbySchedule(memberId);
		return MyStandbyScheduleResponse.create(course, result);
	}

	public ScheduleIdInfo updateReservationStatusToNoShow(Long scheduleId, Long memberId) {
		Schedule schedule = scheduleRepository.findScheduleByTrainerId(memberId, scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));
		schedule.updateReservationStatusToNoShow();
		return ScheduleIdInfo.from(schedule);
	}

	public Boolean registerIndividualSchedule(RegisterScheduleCommand request, Long trainerId) {
		scheduleRepository.findAvailableRegisterSchedule(request, trainerId).ifPresentOrElse(
				schedule -> {
					Member trainer = memberRepository.findById(trainerId)
							.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
					Schedule entity = Schedule.registerSchedule(request.getLessonDt(), trainer, request.getLessonStartTime(), request.getLessonEndTime(), AVAILABLE);
					scheduleRepository.save(entity);
				},
				() -> {
					throw new CustomException(SCHEDULE_ALREADY_EXISTS);
				}
		);
		return true;
	}

	private void changeApplicantAndDeleteStandBy(StandBySchedule standBySchedule, Schedule schedule) {
		schedule.changeApplicantInSchedule(standBySchedule.getMember());
		standByScheduleRepository.delete(standBySchedule);
	}
}

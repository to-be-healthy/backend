package com.tobe.healthy.schedule.application;

import static com.tobe.healthy.config.error.ErrorCode.DATETIME_NOT_VALID;
import static com.tobe.healthy.config.error.ErrorCode.LUNCH_TIME_INVALID;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.NOT_RESERVABLE_SCHEDULE;
import static com.tobe.healthy.config.error.ErrorCode.SCHEDULE_ALREADY_EXISTS;
import static com.tobe.healthy.config.error.ErrorCode.SCHEDULE_LESS_THAN_30_DAYS;
import static com.tobe.healthy.config.error.ErrorCode.SCHEDULE_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.START_DATE_AFTER_END_DATE;
import static com.tobe.healthy.config.error.ErrorCode.TRAINER_NOT_MAPPED;
import static com.tobe.healthy.member.domain.entity.MemberType.STUDENT;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.AVAILABLE;
import static java.time.LocalTime.NOON;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.in.AutoCreateScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.MyReservationResponse;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbyScheduleResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.ScheduleRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		validateScheduleDate(request);

		Member trainer = memberRepository.findById(trainerId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		LocalDate startDt = request.getStartDt();
		while (!startDt.isAfter(request.getEndDt())) {
            LocalTime startTime = request.getStartTime();
            while (!startTime.isAfter(request.getEndTime())) {
				if (request.getClosedDt() != null && request.getClosedDt().equals(startDt)) {
					startDt = startDt.plusDays(1);
					continue;
				}
                if (startTime.equals(request.getLunchStartTime())) {
                    Duration duration = Duration.between(request.getLunchStartTime(), request.getLunchEndTime());
                    startTime = startTime.plusMinutes(duration.toMinutes());
                    continue;
                }
                startTime = startTime.plusMinutes(request.getSessionTime().getDescription());
				if (startTime.equals(request.getEndTime())) {
					break;
				}
				Schedule entity = Schedule.registerSchedule(startDt, trainer, startTime, startTime.plusMinutes(request.getSessionTime().getDescription()), AVAILABLE);
				scheduleRepository.save(entity);
			}
			startDt = startDt.plusDays(1);
		}
		return true;
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

		schedule.registerSchedule(member);
		return ScheduleIdInfo.from(schedule);
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

	public ScheduleIdInfo cancelMemberSchedule(Long scheduleId, Long memberId) {
		Schedule schedule = scheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		// 대기 테이블에 인원이 있으면 수정하기
		Optional<StandBySchedule> standByScheduleOpt = standByScheduleRepository.findByScheduleId(scheduleId);
		if(standByScheduleOpt.isPresent()){
			StandBySchedule standBySchedule = standByScheduleOpt.get();
			changeApplicantAndDeleteStandBy(standBySchedule, schedule);
			return ScheduleIdInfo.create(memberId, schedule, standBySchedule.getMember().getId());
		}else{
			schedule.cancelMemberSchedule();
			return ScheduleIdInfo.from(schedule);
		}
	}

	public List<MyReservationResponse> findAllMyReservation(Long memberId, ScheduleSearchCond searchCond) {
		List<MyReservationResponse> result = scheduleRepository.findAllMyReservation(memberId, searchCond);
		return result.isEmpty() ? null : result;
	}

	public List<MyStandbyScheduleResponse> findAllMyStandbySchedule(Long memberId) {
		List<MyStandbyScheduleResponse> result = scheduleRepository.findAllMyStandbySchedule(memberId);
		return result.isEmpty() ? null : result;
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

//	public Boolean registerClosedDay(RegisterClosedDayCommand request, Long trainerId) {
//		Member trainer = memberRepository.findById(trainerId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
//		request.getLessonDt().stream().forEach(lessonDt -> scheduleRepository.findScheduleByLessonDt(lessonDt).ifPresentOrElse(
//				schedule -> {
//					throw new CustomException(SCHEDULE_ALREADY_EXISTS);
//				},
//				() -> {
//					Schedule entity = Schedule.builder().lessonDt(lessonDt).trainer(trainer).build();
//					scheduleRepository.save(entity);
//				}
//		));
//		return true;
//	}

	private void changeApplicantAndDeleteStandBy(StandBySchedule standBySchedule, Schedule schedule) {
		schedule.changeApplicantInSchedule(standBySchedule.getMember());
		standByScheduleRepository.delete(standBySchedule);
	}
}

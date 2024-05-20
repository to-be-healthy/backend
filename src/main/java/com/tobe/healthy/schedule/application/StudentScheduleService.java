package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.dto.in.RetrieveTrainerScheduleByLessonInfo;
import com.tobe.healthy.schedule.domain.dto.out.MyReservation;
import com.tobe.healthy.schedule.domain.dto.out.MyReservationResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.repository.student.StudentScheduleRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.TRAINER_NOT_MAPPED;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.SOLD_OUT;
import static java.time.LocalTime.NOON;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudentScheduleService {

	private final StudentScheduleRepository studentScheduleRepository;
	private final CourseRepository courseRepository;
	private final TrainerMemberMappingRepository mappingRepository;
	private final CourseService courseService;

	public List<ScheduleCommandResult> findAllByApplicantId(Long memberId) {
		List<ScheduleCommandResult> result = studentScheduleRepository.findAllByApplicantId(memberId);
		return result.isEmpty() ? null : result;
	}

	public ScheduleCommandResponse findAllScheduleOfTrainer(RetrieveTrainerScheduleByLessonInfo searchCond, Member member) {

		TrainerMemberMapping mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(member.getId())
				.orElseThrow(() -> new CustomException(TRAINER_NOT_MAPPED));
		Long trainerId = mapping.getTrainer().getId();
		List<ScheduleCommandResult> list = studentScheduleRepository.findAllSchedule(searchCond, trainerId, member);
		return settingMorningAndAfternoon(list, member);
	}

	private ScheduleCommandResponse settingMorningAndAfternoon(List<ScheduleCommandResult> schedule, Member member) {
		List<ScheduleCommandResult> morning = schedule.stream()
				.filter(s -> NOON.isAfter(s.getLessonStartTime()))
				.peek(s -> { if(isSoldOut(s)) s.setReservationStatus(SOLD_OUT); })
				.collect(Collectors.toList());

		List<ScheduleCommandResult> afternoon = schedule.stream()
				.filter(s -> NOON.isBefore(s.getLessonStartTime()))
				.peek(s -> { if(isSoldOut(s)) s.setReservationStatus(SOLD_OUT); })
				.collect(Collectors.toList());

		return ScheduleCommandResponse.create(member.getScheduleNoticeStatus(), morning, afternoon);
	}

	private boolean isSoldOut(ScheduleCommandResult s) {
		return validateWaitingToday(s) || existsWaiting(s) || beforeLessonDateTimeThenNow(s)
				|| validateWaitingBefore24Hour(s) || validateReservationBefore30Minutes(s);
	}

	//수업시간 24시간 전부터는 대기 불가
	private boolean validateWaitingBefore24Hour(ScheduleCommandResult schedule){
		LocalDateTime before24hour = LocalDateTime.of(schedule.getLessonDt().minusDays(1), schedule.getLessonStartTime());
		return LocalDateTime.now().isAfter(before24hour)
				&& schedule.getApplicantName()!=null
				&& schedule.getWaitingByName()==null;
	}

	//수업시간 30분 전부터는 예약 불가
	private boolean validateReservationBefore30Minutes(ScheduleCommandResult schedule){
		LocalDateTime before30Minutes = LocalDateTime.of(schedule.getLessonDt(), schedule.getLessonStartTime().minusMinutes(30));
		return LocalDateTime.now().isAfter(before30Minutes) && schedule.getApplicantName()==null;
	}

	//현재시간 이전 일정
	private boolean beforeLessonDateTimeThenNow(ScheduleCommandResult schedule){
		LocalDateTime lessonDateTime = LocalDateTime.of(schedule.getLessonDt(), schedule.getLessonStartTime());
		return lessonDateTime.isBefore(LocalDateTime.now());
	}

	//금일 대기 불가
	private boolean validateWaitingToday(ScheduleCommandResult schedule){
		return schedule.getLessonDt().equals(LocalDate.now())
				&& schedule.getApplicantName()!=null
				&& schedule.getWaitingByName()==null;
	}

	//대기자 있음
	private boolean existsWaiting(ScheduleCommandResult schedule){
		return schedule.getApplicantName()!=null
				&& schedule.getWaitingByName()!=null;
	}

	public MyReservationResponse findAllMyReservation(Long memberId, RetrieveTrainerScheduleByLessonInfo searchCond) {
		List<MyReservation> result = studentScheduleRepository.findAllMyReservation(memberId, searchCond);
		return MyReservationResponse.create(courseService.getNowUsingCourse(memberId), result);
	}
}

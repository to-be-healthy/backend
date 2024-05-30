package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.out.FindMyScheduleWaitingResult;
import com.tobe.healthy.schedule.domain.dto.out.MyScheduleWaiting;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.ScheduleWaiting;
import com.tobe.healthy.schedule.repository.schedulewaiting.ScheduleWaitingRepository;
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.schedule.application.TrainerScheduleServiceKt.ONE_DAY;
import static java.time.LocalTime.NOON;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleWaitingService {

	private final MemberRepository memberRepository;
	private final TrainerScheduleRepository trainerScheduleRepository;
	private final ScheduleWaitingRepository scheduleWaitingRepository;
	private final CourseService courseService;

	public String registerScheduleWaiting(Long scheduleId, Long memberId) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Schedule findSchedule = trainerScheduleRepository.findAvailableWaitingId(scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		CourseDto usingCourse = courseService.getNowUsingCourse(memberId);
		if(usingCourse == null) throw new CustomException(COURSE_NOT_FOUND);

		LocalDateTime lessonDateTime = LocalDateTime.of(findSchedule.getLessonDt(), findSchedule.getLessonStartTime());

		if (lessonDateTime.minusDays(ONE_DAY).isAfter(LocalDateTime.now())) {
			ScheduleWaiting scheduleWaiting = ScheduleWaiting.register(member, findSchedule);
			scheduleWaitingRepository.save(scheduleWaiting);
			return getScheduleTimeText(findSchedule.getLessonStartTime());
		} else {
			throw new CustomException(NOT_SCHEDULE_WAITING);
		}
	}

	public String cancelScheduleWaiting(Long scheduleId, Long memberId) {
		Schedule schedule = trainerScheduleRepository.findAvailableWaitingId(scheduleId)
				.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

		LocalDateTime before24Hour = LocalDateTime.of(schedule.getLessonDt().minusDays(1), schedule.getLessonStartTime());
		if (LocalDateTime.now().isAfter(before24Hour)) throw new CustomException(RESERVATION_CANCEL_NOT_VALID);

		ScheduleWaiting scheduleWaiting = scheduleWaitingRepository.findByScheduleIdAndMemberId(scheduleId, memberId)
				.orElseThrow(() -> new CustomException(SCHEDULE_WAITING_NOT_FOUND));
		scheduleWaitingRepository.delete(scheduleWaiting);
		return getScheduleTimeText(scheduleWaiting.getSchedule().getLessonStartTime());
	}

	public FindMyScheduleWaitingResult findAllMyScheduleWaiting(Long memberId) {
		List<MyScheduleWaiting> result = scheduleWaitingRepository.findAllMyScheduleWaiting(memberId);
		return FindMyScheduleWaitingResult.create(courseService.getNowUsingCourse(memberId), result);
	}

	private String getScheduleTimeText(LocalTime lessonStartTime){
		return NOON.isAfter(lessonStartTime) ? "오전 " + lessonStartTime : "오후 " + lessonStartTime;
	}
}

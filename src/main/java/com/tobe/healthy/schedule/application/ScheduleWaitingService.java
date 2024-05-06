package com.tobe.healthy.schedule.application;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.NOT_SCHEDULE_WAITING;
import static com.tobe.healthy.config.error.ErrorCode.SCHEDULE_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.SCHEDULE_WAITING_NOT_FOUND;
import static com.tobe.healthy.schedule.application.TrainerScheduleServiceKt.ONE_DAY;
import static java.time.LocalTime.NOON;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.out.MyScheduleWaiting;
import com.tobe.healthy.schedule.domain.dto.out.MyScheduleWaitingResponse;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.ScheduleWaiting;
import com.tobe.healthy.schedule.repository.schedule_waiting.ScheduleWaitingRepository;
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleWaitingService {

	private final MemberRepository memberRepository;
	private final TrainerScheduleRepository trainerScheduleRepository;
	private final ScheduleWaitingRepository scheduleWaitingRepository;
	private final CourseRepository courseRepository;

	public String registerScheduleWaiting(Long scheduleId, Long memberId) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Schedule findSchedule = trainerScheduleRepository.findAvailableWaitingId(scheduleId)
			.orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

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
		ScheduleWaiting scheduleWaiting = scheduleWaitingRepository.findByScheduleIdAndMemberId(scheduleId, memberId)
				.orElseThrow(() -> new CustomException(SCHEDULE_WAITING_NOT_FOUND));
		scheduleWaitingRepository.delete(scheduleWaiting);
		return getScheduleTimeText(scheduleWaiting.getSchedule().getLessonStartTime());
	}

	public MyScheduleWaitingResponse findAllMyScheduleWaiting(Long memberId) {
		Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, -1);
		CourseDto course = optCourse.map(CourseDto::from).orElse(null);
		List<MyScheduleWaiting> result = scheduleWaitingRepository.findAllMyScheduleWaiting(memberId);
		return MyScheduleWaitingResponse.create(course, result);
	}

	private String getScheduleTimeText(LocalTime lessonStartTime){
		return NOON.isAfter(lessonStartTime) ? "오전 " + lessonStartTime : "오후 " + lessonStartTime;
	}
}

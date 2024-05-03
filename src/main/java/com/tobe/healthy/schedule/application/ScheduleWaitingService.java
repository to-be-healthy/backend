package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbySchedule;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbyScheduleResponse;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.student.StandByScheduleRepository;
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

import static com.tobe.healthy.config.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleWaitingService {

	private final MemberRepository memberRepository;
	private final TrainerScheduleRepository trainerScheduleRepository;
	private final StandByScheduleRepository standByScheduleRepository;
	private final CourseRepository courseRepository;

	public Boolean registerStandBySchedule(Long scheduleId, Long memberId) {

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Schedule schedule = trainerScheduleRepository.findAvailableStandById(scheduleId)
				.orElseThrow(() -> new CustomException(NOT_STAND_BY_SCHEDULE));

		if (!ObjectUtils.isEmpty(schedule.getStandBySchedule())) {
			throw new CustomException(NOT_STAND_BY_SCHEDULE);
		}

		StandBySchedule standBySchedule = StandBySchedule.register(member, schedule);

		standByScheduleRepository.save(standBySchedule);

		return true;
	}

	public Boolean cancelStandBySchedule(Long scheduleId, Long memberId) {
		StandBySchedule standBySchedule = standByScheduleRepository.findByScheduleIdAndMemberId(scheduleId, memberId)
				.orElseThrow(() -> new CustomException(STAND_BY_SCHEDULE_NOT_FOUND));
		standByScheduleRepository.delete(standBySchedule);
		return true;
	}

	public MyStandbyScheduleResponse findAllMyStandbySchedule(Long memberId) {
		Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, -1);
		CourseDto course = optCourse.map(CourseDto::from).orElse(null);
		List<MyStandbySchedule> result = standByScheduleRepository.findAllMyStandbySchedule(memberId);
		return MyStandbyScheduleResponse.create(course, result);
	}
}

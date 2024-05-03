package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static com.tobe.healthy.config.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleWaitingService {

	private final MemberRepository memberRepository;
	private final TrainerScheduleRepository trainerScheduleRepository;
	private final StandByScheduleRepository standByScheduleRepository;

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
}

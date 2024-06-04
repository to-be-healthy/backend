package com.tobe.healthy.schedule.application;

import com.tobe.healthy.common.event.CustomEventPublisher;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.repository.common.CommonScheduleRepository;
import com.tobe.healthy.schedule.repository.waiting.ScheduleWaitingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static com.tobe.healthy.common.event.EventType.SCHEDULE_CANCEL_BY_STUDENT;
import static com.tobe.healthy.config.error.ErrorCode.*;
import static java.time.LocalTime.NOON;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommonScheduleService {

    private final CustomEventPublisher<Long> eventPublisher;

    private final MemberRepository memberRepository;
    private final CommonScheduleRepository commonScheduleRepository;

    public ScheduleIdInfo reserveSchedule(Long scheduleId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        Schedule schedule = commonScheduleRepository.findAvailableScheduleById(scheduleId)
                .orElseThrow(() -> new CustomException(NOT_RESERVABLE_SCHEDULE));

        LocalDateTime before30Minutes = LocalDateTime.of(schedule.getLessonDt(), schedule.getLessonStartTime().minusMinutes(30));
        if (LocalDateTime.now().isAfter(before30Minutes)) throw new CustomException(RESERVATION_NOT_VALID);

        schedule.registerSchedule(member);
        return ScheduleIdInfo.create(schedule, getScheduleTimeText(schedule.getLessonStartTime()));
    }

    public ScheduleIdInfo cancelMemberSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = commonScheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
                .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

        LocalDateTime before24Hour = LocalDateTime.of(schedule.getLessonDt().minusDays(1), schedule.getLessonStartTime());
        if (LocalDateTime.now().isAfter(before24Hour)) throw new CustomException(RESERVATION_CANCEL_NOT_VALID);

        ScheduleIdInfo idInfo = ScheduleIdInfo.create(schedule, getScheduleTimeText(schedule.getLessonStartTime()));
        schedule.cancelMemberSchedule();
        eventPublisher.publish(scheduleId, SCHEDULE_CANCEL_BY_STUDENT);
        return idInfo;
    }

    private String getScheduleTimeText(LocalTime lessonStartTime){
        return NOON.isAfter(lessonStartTime) ? "오전 " + lessonStartTime : "오후 " + lessonStartTime;
    }
}

package com.tobe.healthy.schedule.application;

import com.tobe.healthy.common.event.CustomEventPublisher;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.notification.domain.dto.in.CommandSendNotification;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.repository.common.CommonScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.tobe.healthy.common.event.EventType.NOTIFICATION;
import static com.tobe.healthy.common.event.EventType.SCHEDULE_CANCEL;
import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.notification.domain.entity.NotificationCategory.SCHEDULE;
import static com.tobe.healthy.notification.domain.entity.NotificationType.CANCEL;
import static com.tobe.healthy.notification.domain.entity.NotificationType.RESERVE;
import static java.time.LocalTime.NOON;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommonScheduleService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일(E) h시");

    private final CustomEventPublisher<Long> eventPublisher;
    private final CustomEventPublisher<CommandSendNotification> notificationPublisher;

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

        // 일정 신청시 알림
        CommandSendNotification notification = new CommandSendNotification(
                RESERVE.getDescription(),
                String.format("%s님이 %s에 예약했어요.", schedule.getApplicant().getName(), LocalDateTime.of(schedule.getLessonDt(), schedule.getLessonStartTime()).format(formatter)),
                List.of(schedule.getTrainer().getId()),
                RESERVE,
                SCHEDULE,
                null
        );

        notificationPublisher.publish(notification, NOTIFICATION);

        return ScheduleIdInfo.create(schedule, getScheduleTimeText(schedule.getLessonStartTime()));
    }

    public ScheduleIdInfo cancelMemberSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = commonScheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
                .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

        LocalDateTime before24Hour = LocalDateTime.of(schedule.getLessonDt().minusDays(1), schedule.getLessonStartTime());
        if (LocalDateTime.now().isAfter(before24Hour)) throw new CustomException(RESERVATION_CANCEL_NOT_VALID);

        // 일정 취소시 알림
        CommandSendNotification notification = new CommandSendNotification(
                CANCEL.getDescription(),
                String.format("%s님이 %s 예약을 취소했어요.", schedule.getApplicant().getName(), LocalDateTime.of(schedule.getLessonDt(), schedule.getLessonStartTime()).format(formatter)),
                List.of(schedule.getTrainer().getId()),
                CANCEL,
                SCHEDULE,
                null
        );

        notificationPublisher.publish(notification, NOTIFICATION);

        ScheduleIdInfo idInfo = ScheduleIdInfo.create(schedule, getScheduleTimeText(schedule.getLessonStartTime()));
        schedule.cancelMemberSchedule();


        eventPublisher.publish(scheduleId, SCHEDULE_CANCEL);
        return idInfo;
    }

    private String getScheduleTimeText(LocalTime lessonStartTime){
        return NOON.isAfter(lessonStartTime) ? "오전 " + lessonStartTime : "오후 " + lessonStartTime;
    }
}

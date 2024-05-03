package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.student.CommonScheduleRepository;
import com.tobe.healthy.schedule.repository.student.StandByScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static com.tobe.healthy.config.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommonScheduleService {
    private final MemberRepository memberRepository;
    private final CommonScheduleRepository commonScheduleRepository;
    private final StandByScheduleRepository standByScheduleRepository;

    public ScheduleIdInfo reserveSchedule(Long scheduleId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        Schedule schedule = commonScheduleRepository.findAvailableScheduleById(scheduleId)
                .orElseThrow(() -> new CustomException(NOT_RESERVABLE_SCHEDULE));

        LocalTime before24Hour = schedule.getLessonStartTime().minusMinutes(30);
        if (LocalTime.now().isAfter(before24Hour)) throw new CustomException(RESERVATION_NOT_VALID);

        schedule.registerSchedule(member);
        return ScheduleIdInfo.from(schedule);
    }

    public ScheduleIdInfo cancelMemberSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = commonScheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
                .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        LocalDateTime before24Hour = LocalDateTime.of(schedule.getLessonDt().minusDays(1), schedule.getLessonStartTime());
        if (now.isAfter(before24Hour)) throw new CustomException(RESERVATION_CANCEL_NOT_VALID);

        // 대기 테이블에 인원이 있으면 수정하기
        Optional<StandBySchedule> standByScheduleOpt = standByScheduleRepository.findByScheduleId(scheduleId);
        if (standByScheduleOpt.isPresent()) {
            StandBySchedule standBySchedule = standByScheduleOpt.get();
            changeApplicantAndDeleteStandBy(standBySchedule, schedule);
            return ScheduleIdInfo.create(memberId, schedule, standBySchedule.getMember().getId());
        } else {
            ScheduleIdInfo idInfo = ScheduleIdInfo.from(schedule);
            schedule.cancelMemberSchedule();
            return idInfo;
        }
    }

    private void changeApplicantAndDeleteStandBy(StandBySchedule standBySchedule, Schedule schedule) {
        schedule.changeApplicantInSchedule(standBySchedule.getMember());
        standByScheduleRepository.delete(standBySchedule);
    }
}

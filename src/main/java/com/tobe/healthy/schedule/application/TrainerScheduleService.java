package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.entity.Course;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleCommand;
import com.tobe.healthy.schedule.domain.dto.in.RegisterScheduleRequest;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbySchedule;
import com.tobe.healthy.schedule.domain.dto.out.MyStandbyScheduleResponse;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import com.tobe.healthy.schedule.repository.trainer.TrainerScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.AVAILABLE;
import static java.time.Duration.between;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrainerScheduleService {
    private final MemberRepository memberRepository;
    private final TrainerScheduleRepository trainerScheduleRepository;
    private final StandByScheduleRepository standByScheduleRepository;
    private final CourseRepository courseRepository;

    public Boolean registerSchedule(RegisterScheduleRequest request, Long trainerId) {
        validateScheduleDate(request);

        Member trainer = memberRepository.findById(trainerId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        LocalDate lessonDt = request.getStartDt();

        while (islessonDtBeforeOrEqualsEndDt(request, lessonDt)) {
            LocalTime startTime = request.getStartTime();
            while (startTimeIsBefore(request, startTime)) {
                LocalTime endTime = startTime.plusMinutes(request.getSessionTime().getDescription());

                if (endTime.isAfter(request.getEndTime())) {
                    break;
                }

                if (request.getClosedDt().contains(lessonDt)) {
                    lessonDt = lessonDt.plusDays(1);
                    continue;
                }

                if (isStartTimeEqualsLunchStartTime(request, startTime)) {
                    Duration duration = between(request.getLunchStartTime(), request.getLunchEndTime());
                    startTime = startTime.plusMinutes(duration.toMinutes());
                    continue;
                }

                Boolean isDuplicateSchedule = trainerScheduleRepository.validateRegisterSchedule(lessonDt, startTime, endTime, trainerId);

                if (isDuplicateSchedule) {
                    throw new CustomException(SCHEDULE_ALREADY_EXISTS);
                }

                Schedule schedule = Schedule.registerSchedule(lessonDt, trainer, startTime, endTime, AVAILABLE);
                trainerScheduleRepository.save(schedule);

                startTime = endTime;
            }
            lessonDt = lessonDt.plusDays(1);
        }
        return true;
    }

    private boolean isStartTimeEqualsLunchStartTime(RegisterScheduleRequest request, LocalTime startTime) {
        return startTime.equals(request.getLunchStartTime());
    }

    private boolean startTimeIsBefore(RegisterScheduleRequest request, LocalTime startTime) {
        return startTime.isBefore(request.getEndTime());
    }

    private boolean islessonDtBeforeOrEqualsEndDt(RegisterScheduleRequest request, LocalDate lessonDt) {
        return !lessonDt.isAfter(request.getEndDt());
    }

    private void validateScheduleDate(RegisterScheduleRequest request) {
        if (request.getStartDt().isAfter(request.getEndDt())) {
            throw new CustomException(START_DATE_AFTER_END_DATE);
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new CustomException(DATETIME_NOT_VALID);
        }
        if (request.getLunchStartTime().isAfter(request.getLunchEndTime())) {
            throw new CustomException(LUNCH_TIME_INVALID);
        }
        if (DAYS.between(request.getStartDt(), request.getEndDt()) > 30) {
            throw new CustomException(SCHEDULE_LESS_THAN_30_DAYS);
        }
    }

    public ScheduleIdInfo reserveSchedule(Long scheduleId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        Schedule schedule = trainerScheduleRepository.findAvailableScheduleById(scheduleId)
                .orElseThrow(() -> new CustomException(NOT_RESERVABLE_SCHEDULE));

        LocalTime before24Hour = schedule.getLessonStartTime().minusMinutes(30);
        if (LocalTime.now().isAfter(before24Hour)) throw new CustomException(RESERVATION_NOT_VALID);

        schedule.registerSchedule(member);
        return ScheduleIdInfo.from(schedule);
    }

    public List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond, Member trainer) {
        return trainerScheduleRepository.findAllSchedule(searchCond, trainer.getId(), trainer);
    }

    public Boolean cancelTrainerSchedule(Long scheduleId, Long memberId) {
        Schedule entity = trainerScheduleRepository.findScheduleByTrainerId(memberId, scheduleId)
                .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

        entity.cancelTrainerSchedule();

        return true;
    }

    public ScheduleIdInfo cancelMemberSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = trainerScheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
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

    public MyStandbyScheduleResponse findAllMyStandbySchedule(Long memberId) {
        Optional<Course> optCourse = courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(memberId, -1);
        CourseDto course = optCourse.map(CourseDto::from).orElse(null);
        List<MyStandbySchedule> result = trainerScheduleRepository.findAllMyStandbySchedule(memberId);
        return MyStandbyScheduleResponse.create(course, result);
    }

    public ScheduleIdInfo updateReservationStatusToNoShow(Long scheduleId, Long memberId) {
        Schedule schedule = trainerScheduleRepository.findScheduleByTrainerId(memberId, scheduleId)
                .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));
        schedule.updateReservationStatusToNoShow();
        return ScheduleIdInfo.from(schedule);
    }

    public Boolean registerIndividualSchedule(RegisterScheduleCommand request, Long trainerId) {
        trainerScheduleRepository.findAvailableRegisterSchedule(request, trainerId).ifPresentOrElse(
                schedule -> {
                    Member trainer = memberRepository.findById(trainerId)
                            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
                    Schedule entity = Schedule.registerSchedule(request.getLessonDt(), trainer, request.getLessonStartTime(), request.getLessonEndTime(), AVAILABLE);
                    trainerScheduleRepository.save(entity);
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

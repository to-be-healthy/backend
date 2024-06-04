package com.tobe.healthy.common.event;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.in.CourseUpdateCommand;
import com.tobe.healthy.course.repository.CourseRepository;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.ScheduleWaiting;
import com.tobe.healthy.schedule.repository.common.CommonScheduleRepository;
import com.tobe.healthy.schedule.repository.waiting.ScheduleWaitingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.course.domain.entity.CourseHistoryType.RESERVATION;
import static com.tobe.healthy.point.domain.entity.Calculation.MINUS;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomEventListener {

    private final int ONE_LESSON = 1;

    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final CommonScheduleRepository commonScheduleRepository;
    private final ScheduleWaitingRepository scheduleWaitingRepository;

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleEvent(CustomEvent event) {
        switch (event.getType()){
            case SCHEDULE_CANCEL -> changeWaitingToCompleted((Long) event.getResult());
        }
    }

    public void changeWaitingToCompleted(Long scheduleId) {
        // 대기자 있으면 예약으로 변경
        Optional<ScheduleWaiting> scheduleWaitingOpt = scheduleWaitingRepository.findByScheduleId(scheduleId);
        if(scheduleWaitingOpt.isPresent()){
            ScheduleWaiting scheduleWaiting = scheduleWaitingOpt.get();
            scheduleWaitingRepository.delete(scheduleWaiting);
            Schedule schedule = commonScheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

            //수업시간 24시간 이전인 경우만 대기 -> 예약으로 변경 가능
            if(!isBefore24Hour(schedule)) return;

            //수강권 유효성 검사
            Long waitingMemberId = scheduleWaiting.getMember().getId();
            courseRepository.findTop1ByMemberIdAndRemainLessonCntGreaterThanOrderByCreatedAtDesc(waitingMemberId, 0)
                .ifPresent(i -> {
                    minusCourse(waitingMemberId, scheduleId, schedule.getTrainer().getId());
                    schedule.registerSchedule(scheduleWaiting.getMember());
                    commonScheduleRepository.save(schedule);
                });
        }
    }

    private boolean isBefore24Hour(Schedule schedule){
        LocalDateTime before24Hour = LocalDateTime.of(schedule.getLessonDt().minusDays(1), schedule.getLessonStartTime());
        return LocalDateTime.now().isBefore(before24Hour);
    }

    private void minusCourse(Long studentId, Long scheduleId, Long trainerId) {
        CourseUpdateCommand command = CourseUpdateCommand.create(studentId, MINUS, RESERVATION, ONE_LESSON);
        courseService.updateCourseByMember(scheduleId, trainerId, command);
    }

}

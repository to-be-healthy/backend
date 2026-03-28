package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.lessonhistory.domain.dto.in.UnwrittenLessonHistorySearchCond;
import com.tobe.healthy.schedule.domain.dto.in.CommandRegisterSchedule;
import com.tobe.healthy.schedule.domain.dto.out.FeedbackNotificationToTrainer;
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult;
import com.tobe.healthy.schedule.domain.entity.ReservationStatus;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerScheduleRepositoryCustom {

    RetrieveTrainerScheduleByLessonDtResult findOneTrainerTodaySchedule(String lessonDt, Long trainerId);

    boolean validateDuplicateSchedule(
            TrainerScheduleInfo trainerScheduleInfo,
            CommandRegisterSchedule request,
            Long trainerId
    );

    Optional<Schedule> findAvailableWaitingId(Long scheduleId);

    List<Schedule> findAllSchedule(
            String lessonDt,
            LocalDate lessonStartDt,
            LocalDate lessonEndDt,
            Long trainerId
    );

    List<Schedule> findAllSchedule(
            List<Long> scheduleIds,
            List<ReservationStatus> reservationStatus,
            Long trainerId
    );

    Schedule findAllSchedule(Long scheduleId, ReservationStatus reservationStatus, Long trainerId);

    List<Schedule> findAllDisabledSchedule(LocalDate lessonStartDt, LocalDate lessonEndDt);

    List<Schedule> findAllUnwrittenLessonHistory(UnwrittenLessonHistorySearchCond request, Long memberId);

    List<Schedule> findAllSimpleLessonHistoryByMemberId(Long studentId, Long trainerId);

    Page<Schedule> findAllScheduleByStudentId(Long studentId, Pageable pageable, Long trainerId);

    List<FeedbackNotificationToTrainer> findAllFeedbackNotificationToTrainer();
}

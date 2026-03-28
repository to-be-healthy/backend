package com.tobe.healthy.schedule.application;

import com.tobe.healthy.common.KotlinCustomPaging;
import com.tobe.healthy.schedule.domain.dto.in.RetrieveTrainerScheduleByLessonDt;
import com.tobe.healthy.schedule.domain.dto.in.RetrieveTrainerScheduleByLessonInfo;
import com.tobe.healthy.schedule.domain.dto.in.RetrieveTrainerScheduleByTrainerId;
import com.tobe.healthy.schedule.domain.dto.out.RetrieveApplicantSchedule;
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerDefaultLessonTimeResult;
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult;
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonInfoResult;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo;
import com.tobe.healthy.schedule.repository.TrainerScheduleInfoRepository;
import com.tobe.healthy.schedule.repository.TrainerScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TrainerScheduleService {

    private final TrainerScheduleRepository trainerScheduleRepository;
    private final TrainerScheduleInfoRepository trainerScheduleInfoRepository;

    public RetrieveTrainerDefaultLessonTimeResult findOneDefaultLessonTime(Long trainerId) {
        TrainerScheduleInfo trainerScheduleInfo = trainerScheduleInfoRepository.findOneByTrainerId(trainerId);
        return RetrieveTrainerDefaultLessonTimeResult.from(trainerScheduleInfo);
    }

    public RetrieveTrainerScheduleByLessonInfoResult findAllSchedule(
            RetrieveTrainerScheduleByLessonInfo request,
            Long trainerId
    ) {
        List<Schedule> schedules = trainerScheduleRepository.findAllSchedule(
                request.getLessonDt(),
                request.getLessonStartDt(),
                request.getLessonEndDt(),
                trainerId
        );
        return RetrieveTrainerScheduleByLessonInfoResult.from(schedules);
    }

    public RetrieveTrainerScheduleByLessonInfoResult findAllSchedule(
            Long trainerId,
            RetrieveTrainerScheduleByTrainerId request
    ) {
        List<Schedule> schedules = trainerScheduleRepository.findAllSchedule(
                null,
                request.getLessonStartDt(),
                request.getLessonEndDt(),
                trainerId
        );
        return RetrieveTrainerScheduleByLessonInfoResult.from(schedules);
    }

    public RetrieveTrainerScheduleByLessonDtResult findOneTrainerTodaySchedule(
            RetrieveTrainerScheduleByLessonDt request,
            Long trainerId
    ) {
        return trainerScheduleRepository.findOneTrainerTodaySchedule(request.getLessonDt(), trainerId);
    }

    public KotlinCustomPaging<RetrieveApplicantSchedule> findAllScheduleByStudentId(
            Long studentId,
            Pageable pageable,
            Long trainerId
    ) {
        Page<Schedule> schedules = trainerScheduleRepository.findAllScheduleByStudentId(studentId, pageable, trainerId);
        Page<RetrieveApplicantSchedule> contents = schedules.map(RetrieveApplicantSchedule::from);

        return new KotlinCustomPaging<>(
                contents.getContent(),
                contents.getPageable().getPageNumber(),
                contents.getPageable().getPageSize(),
                contents.getTotalPages(),
                contents.getTotalElements(),
                contents.isLast()
        );
    }
}

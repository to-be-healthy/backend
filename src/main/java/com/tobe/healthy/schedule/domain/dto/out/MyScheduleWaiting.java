package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.ScheduleWaiting;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MyScheduleWaiting {
    private Long scheduleId;
    private String trainerName;
    private LocalDate lessonDt;
    private LocalTime lessonStartTime;
    private LocalTime lessonEndTime;
    private String reservationStatus;

    public static MyScheduleWaiting from(ScheduleWaiting scheduleWaiting) {
        return MyScheduleWaiting.builder()
                .scheduleId(scheduleWaiting.getSchedule().getId())
                .trainerName(scheduleWaiting.getSchedule().getTrainer().getName() + " 트레이너")
                .lessonDt(scheduleWaiting.getSchedule().getLessonDt())
                .lessonStartTime(scheduleWaiting.getSchedule().getLessonStartTime())
                .lessonEndTime(scheduleWaiting.getSchedule().getLessonEndTime())
                .build();
    }
}

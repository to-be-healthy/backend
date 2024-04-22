package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MyStandbyScheduleResponse {
    private Long scheduleId;
    private String trainerName;
    private LocalDate lessonDt;
    private LocalTime lessonStartTime;
    private LocalTime lessonEndTime;
    private int round;
    private String reservationStatus;

    public static MyStandbyScheduleResponse from(StandBySchedule standBySchedule) {
        return MyStandbyScheduleResponse.builder()
                .scheduleId(standBySchedule.getSchedule().getId())
                .trainerName(standBySchedule.getSchedule().getTrainer().getName() + " 트레이너")
                .lessonDt(standBySchedule.getSchedule().getLessonDt())
                .lessonStartTime(standBySchedule.getSchedule().getLessonStartTime())
                .lessonEndTime(standBySchedule.getSchedule().getLessonEndTime())
                .round(standBySchedule.getSchedule().getRound())
                .build();
    }
}

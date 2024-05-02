package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MyStandbySchedule {
    private Long scheduleId;
    private String trainerName;
    private LocalDate lessonDt;
    private LocalTime lessonStartTime;
    private LocalTime lessonEndTime;
    private String reservationStatus;

    public static MyStandbySchedule from(StandBySchedule standBySchedule) {
        return MyStandbySchedule.builder()
                .scheduleId(standBySchedule.getSchedule().getId())
                .trainerName(standBySchedule.getSchedule().getTrainer().getName() + " 트레이너")
                .lessonDt(standBySchedule.getSchedule().getLessonDt())
                .lessonStartTime(standBySchedule.getSchedule().getLessonStartTime())
                .lessonEndTime(standBySchedule.getSchedule().getLessonEndTime())
                .build();
    }
}

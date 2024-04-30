package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MyReservationResponse {
    private Long scheduleId;
    private LocalDate lessonDt;
    private LocalTime lessonStartTime;
    private LocalTime lessonEndTime;
    private String trainerName;
    private String reservationStatus;

    public static MyReservationResponse from(Schedule schedule) {
        return MyReservationResponse.builder().scheduleId(schedule.getId())
                .lessonDt(schedule.getLessonDt())
                .lessonStartTime(schedule.getLessonStartTime())
                .lessonEndTime(schedule.getLessonEndTime())
                .trainerName(schedule.getTrainer().getName() + " 트레이너")
                .reservationStatus(schedule.getReservationStatus().name())
                .build();
    }
}

package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRegisterScheduleResult {

    @Builder.Default
    private List<LocalDate> lessonDt = new ArrayList<>();
    private LocalTime lessonStartTime;
    private LocalTime lessonEndTime;
    private int lessonTime;
    private LocalTime lunchStartTime;
    private LocalTime lunchEndTime;

    public static CommandRegisterScheduleResult from(
            List<Schedule> schedule,
            TrainerScheduleInfo trainerScheduleInfo
    ) {
        return CommandRegisterScheduleResult.builder()
                .lessonDt(schedule.stream()
                        .map(Schedule::getLessonDt)
                        .distinct()
                        .collect(Collectors.toList()))
                .lessonStartTime(trainerScheduleInfo.getLessonStartTime())
                .lessonEndTime(trainerScheduleInfo.getLessonEndTime())
                .lessonTime(trainerScheduleInfo.getLessonTime().getDescription())
                .lunchStartTime(trainerScheduleInfo.getLunchStartTime())
                .lunchEndTime(trainerScheduleInfo.getLunchEndTime())
                .build();
    }
}

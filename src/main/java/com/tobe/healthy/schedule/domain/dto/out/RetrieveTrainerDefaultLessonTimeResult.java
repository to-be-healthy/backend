package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.schedule.domain.entity.TrainerScheduleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveTrainerDefaultLessonTimeResult {

    private String lessonStartTime;
    private String lessonEndTime;
    private String lunchStartTime;
    private String lunchEndTime;
    private Integer lessonTime;
    @Builder.Default
    private List<DayOfWeek> closedDays = new ArrayList<>();

    public static RetrieveTrainerDefaultLessonTimeResult from(TrainerScheduleInfo trainerScheduleInfo) {
        if (trainerScheduleInfo == null) {
            return RetrieveTrainerDefaultLessonTimeResult.builder()
                    .lessonStartTime(LessonTimeFormatter.formatLessonTime(null))
                    .lessonEndTime(LessonTimeFormatter.formatLessonTime(null))
                    .lunchStartTime(LessonTimeFormatter.formatLessonTime(null))
                    .lunchEndTime(LessonTimeFormatter.formatLessonTime(null))
                    .lessonTime(null)
                    .closedDays(new ArrayList<>())
                    .build();
        }
        return RetrieveTrainerDefaultLessonTimeResult.builder()
                .lessonStartTime(LessonTimeFormatter.formatLessonTime(trainerScheduleInfo.getLessonStartTime()))
                .lessonEndTime(LessonTimeFormatter.formatLessonTime(trainerScheduleInfo.getLessonEndTime()))
                .lunchStartTime(LessonTimeFormatter.formatLessonTime(trainerScheduleInfo.getLunchStartTime()))
                .lunchEndTime(LessonTimeFormatter.formatLessonTime(trainerScheduleInfo.getLunchEndTime()))
                .lessonTime(trainerScheduleInfo.getLessonTime().getDescription())
                .closedDays(
                        trainerScheduleInfo.getTrainerScheduleClosedDays() != null
                                ? trainerScheduleInfo.getTrainerScheduleClosedDays().stream()
                                .map(day -> day.getClosedDays())
                                .collect(Collectors.toList())
                                : new ArrayList<>()
                )
                .build();
    }
}

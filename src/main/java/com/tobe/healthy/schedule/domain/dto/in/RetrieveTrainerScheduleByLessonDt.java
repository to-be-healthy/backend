package com.tobe.healthy.schedule.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveTrainerScheduleByLessonDt {

    @Schema(description = "조회할 수업 일자", example = "2024-04-01")
    private String lessonDt;

    public static RetrieveTrainerScheduleByLessonDt of(String lessonDt) {
        return RetrieveTrainerScheduleByLessonDt.builder()
                .lessonDt(lessonDt)
                .build();
    }
}

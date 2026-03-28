package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonInfoResult.LessonDetailResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveTrainerScheduleByLessonDtResult {

    private String trainerName;
    private Long scheduleTotalCount;
    @Builder.Default
    private List<LessonDetailResult> schedule = new ArrayList<>();
}

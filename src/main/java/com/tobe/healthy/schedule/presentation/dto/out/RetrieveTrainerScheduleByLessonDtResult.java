package com.tobe.healthy.schedule.presentation.dto.out;

import java.util.ArrayList;
import java.util.List;

import com.tobe.healthy.schedule.presentation.dto.out.RetrieveTrainerScheduleByLessonInfoResult.LessonDetailResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

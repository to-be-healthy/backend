package com.tobe.healthy.workout.domain.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedExerciseDto {

    private Long exerciseId;
    @Positive(message = "숫자를 입력해주세요.")
    private int setNum;
    @Positive(message = "숫자를 입력해주세요.")
    private int weight;
    @Positive(message = "숫자를 입력해주세요.")
    private int numberOfCycles;

}

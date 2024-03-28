package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.Instructions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructionsDto {

    private Long exerciseId;
    private Long instructionsId;
    private String instructions;

    public static InstructionsDto from(Instructions instructions) {
        return InstructionsDto.builder()
                .instructionsId(instructions.getInstructionsId())
                .instructions(instructions.getInstructions())
                .exerciseId(instructions.getExerciseId())
                .build();
    }
}

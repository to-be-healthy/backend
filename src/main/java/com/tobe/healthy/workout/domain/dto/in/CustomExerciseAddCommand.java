package com.tobe.healthy.workout.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomExerciseAddCommand {

    @Schema(description = "운동명" , example = "버피테스트")
    @NotBlank
    private String names;

    @Schema(description = "사용근육" , example = "전신")
    private String muscles;

}

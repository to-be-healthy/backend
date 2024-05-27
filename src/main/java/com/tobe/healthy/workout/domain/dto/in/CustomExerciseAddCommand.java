package com.tobe.healthy.workout.domain.dto.in;

import com.tobe.healthy.workout.domain.entity.exercise.ExerciseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CustomExerciseAddCommand {

    @Schema(description = "카테고리" , example = "CORE")
    @NotNull
    private ExerciseCategory category;

    @Schema(description = "운동명" , example = "버피테스트")
    @NotBlank
    private String names;

    @Schema(description = "사용근육" , example = "전신")
    private String muscles;

}

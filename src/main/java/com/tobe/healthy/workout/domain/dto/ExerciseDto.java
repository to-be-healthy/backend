package com.tobe.healthy.workout.domain.dto;

import com.tobe.healthy.workout.domain.entity.Exercise;
import com.tobe.healthy.workout.domain.entity.ExerciseCategory;
import com.tobe.healthy.workout.domain.entity.PrimaryMuscle;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {

    private Long exerciseId;
    private ExerciseCategory category;
    private PrimaryMuscle primaryMuscle;
    private String equipments;
    private String forces;
    private String levels;
    private String mechanics;
    private String names;
    private String subId;
    @Builder.Default
    private List<String> instructions = new ArrayList<>();

    public static ExerciseDto from(Exercise exercise) {
        return ExerciseDto.builder()
                .exerciseId(exercise.getExerciseId())
                .category(exercise.getCategory())
                .primaryMuscle(exercise.getPrimaryMuscle())
                .equipments(exercise.getEquipments())
                .forces(exercise.getForces())
                .levels(exercise.getLevels())
                .mechanics(exercise.getMechanics())
                .names(exercise.getNames())
                .subId(exercise.getSubId())
                .build();
    }

}

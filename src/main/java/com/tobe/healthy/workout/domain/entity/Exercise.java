package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.workout.domain.dto.ExerciseDto;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;
import lombok.Builder;

@Entity
@Table(name = "exercise")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long exerciseId;

    @Enumerated(STRING)
    private ExerciseCategory category;

    @Enumerated(STRING)
    private PrimaryMuscle primaryMuscle;

    private String equipments;
    private String forces;
    private String levels;
    private String mechanics;
    private String names;
    private String subId;

    public static Exercise from(ExerciseDto exerciseDto) {
        return Exercise.builder()
                .exerciseId(exerciseDto.getExerciseId())
                .category(exerciseDto.getCategory())
                .primaryMuscle(exerciseDto.getPrimaryMuscle())
                .equipments(exerciseDto.getEquipments())
                .forces(exerciseDto.getForces())
                .levels(exerciseDto.getLevels())
                .mechanics(exerciseDto.getMechanics())
                .names(exerciseDto.getNames())
                .subId(exerciseDto.getSubId())
                .build();
    }

}

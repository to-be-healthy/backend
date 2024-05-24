package com.tobe.healthy.workout.domain.entity.exercise;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;

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

    private String names;

    @Enumerated(STRING)
    private ExerciseCategory category;

    private String primaryMuscle;
    private String secondaryMuscle;

}

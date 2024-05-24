package com.tobe.healthy.workout.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "exercise_custom")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class ExerciseCustom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_custom_id")
    private Long exerciseCustomId;

    private String names;

    @Enumerated(STRING)
    private ExerciseCategory category;

    private String muscles;

}

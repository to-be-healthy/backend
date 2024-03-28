package com.tobe.healthy.workout.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "secondary_muscles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class SecondaryMuscles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "secondary_muscles_id")
    private Long secondaryMusclesId;

    private String secondaryMuscles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

}

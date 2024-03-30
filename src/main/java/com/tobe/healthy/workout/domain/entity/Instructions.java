package com.tobe.healthy.workout.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instructions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Instructions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instructions_id")
    private Long instructionsId;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    private Long exerciseId;

}

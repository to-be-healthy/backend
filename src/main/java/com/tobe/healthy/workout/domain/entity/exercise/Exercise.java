package com.tobe.healthy.workout.domain.entity.exercise;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.in.CustomExerciseAddCommand;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "exercise")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long exerciseId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private Member member;

    private String names;

    @Enumerated(STRING)
    private ExerciseCategory category;

    private String primaryMuscle;
    private String secondaryMuscle;

    public static Exercise create(Member member, CustomExerciseAddCommand command) {
        return Exercise.builder()
                .member(member)
                .names(command.getNames())
                .category(command.getCategory())
                .secondaryMuscle(command.getMuscles())
                .build();
    }
}

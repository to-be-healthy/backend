package com.tobe.healthy.workout.domain.entity.exercise;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.in.CustomExerciseAddCommand;
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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(STRING)
    private ExerciseCategory category;

    private String names;

    private String muscles;

    public static ExerciseCustom create(Member member, CustomExerciseAddCommand command){
        return ExerciseCustom.builder()
                .member(member)
                .category(command.getCategory())
                .names(command.getNames())
                .muscles(command.getMuscles())
                .build();
    }

}

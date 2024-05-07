package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.workout.domain.dto.CompletedExerciseDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "completed_exercise")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class CompletedExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completed_id")
    private Long completedId;

    private Long exerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_history_id")
    private WorkoutHistory workoutHistory;

    private String name;
    private int setNum;
    private int weight;
    private int numberOfCycles;

    public static CompletedExercise create(CompletedExerciseDto completed, WorkoutHistory history, String name) {
        return CompletedExercise.builder()
                .exerciseId(completed.getExerciseId())
                .name(name)
                .setNum(completed.getSetNum())
                .weight(completed.getWeight())
                .numberOfCycles(completed.getNumberOfCycles())
                .workoutHistory(history)
                .build();
    }

}

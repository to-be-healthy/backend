package com.tobe.healthy.workout.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.function.Consumer;


@Entity
@Table(name = "workout_history_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class WorkoutHistoryLike {

    @EmbeddedId
    private WorkoutHistoryLikePK workoutHistoryLikePK;

    public static WorkoutHistoryLike from(WorkoutHistoryLikePK workoutHistoryLikePK) {
        return WorkoutHistoryLike.builder()
                .workoutHistoryLikePK(workoutHistoryLikePK)
                .build();
    }

}

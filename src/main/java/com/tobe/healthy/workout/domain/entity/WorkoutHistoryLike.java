package com.tobe.healthy.workout.domain.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


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

package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class WorkoutHistory extends BaseTimeEntity<WorkoutHistory, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workout_history_id")
    private Long workoutHistoryId;

    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static WorkoutHistory create(WorkoutHistoryDto historyDto, Member member) {
        return WorkoutHistory.builder()
                .workoutHistoryId(historyDto.getWorkoutHistoryId())
                .content(historyDto.getContent())
                .member(member)
                .build();
    }
}

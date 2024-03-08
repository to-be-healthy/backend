package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_history_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class WorkoutHistoryComment extends BaseTimeEntity<WorkoutHistoryComment, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_history_id")
    private WorkoutHistory workoutHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    public static WorkoutHistoryComment create(WorkoutHistory history, Member member, String content) {
        return WorkoutHistoryComment.builder()
                .workoutHistory(history)
                .member(member)
                .content(content)
                .build();
    }
}

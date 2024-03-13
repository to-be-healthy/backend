package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @ColumnDefault("false")
    @Builder.Default
    private Boolean delYn = false;

    public static WorkoutHistoryComment create(WorkoutHistory history, Member member, String content) {
        return WorkoutHistoryComment.builder()
                .workoutHistory(history)
                .member(member)
                .content(content)
                .build();
    }

    public void deleteWorkoutHistoryComment() {
        this.delYn = true;
    }

    public void updateContent(String content){
        this.content = content;
    }
}

package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    private Long trainerId;

    @OneToMany(mappedBy = "workoutHistory", cascade = CascadeType.ALL)
    private List<WorkoutHistoryFile> historyFiles = new ArrayList<>();

    public static WorkoutHistory create(WorkoutHistoryDto historyDto, Member member) {
        return WorkoutHistory.builder()
                .workoutHistoryId(historyDto.getWorkoutHistoryId())
                .content(historyDto.getContent())
                .member(member)
                .trainerId(historyDto.getTrainerId())
                .build();
    }

    public void updateContent(String content){
        this.content = content;
    }

}

package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.file.domain.entity.WorkoutHistoryFile;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.workout.domain.dto.WorkoutHistoryDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tobe.healthy.schedule.domain.entity.ReservationStatus.COMPLETED;
import static jakarta.persistence.FetchType.LAZY;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    private Member trainer;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean delYn = false;

    @ColumnDefault("0")
    @Builder.Default
    private Long likeCnt = 0L;

    @ColumnDefault("0")
    @Builder.Default
    private Long commentCnt = 0L;

    @Builder.Default
    @OneToMany(mappedBy = "workoutHistory", cascade = CascadeType.ALL)
    private List<WorkoutHistoryFile> historyFiles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "workoutHistory", cascade = CascadeType.ALL)
    private List<WorkoutHistoryComment> historyComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "workoutHistory", cascade = CascadeType.ALL)
    private List<CompletedExercise> completedExercises = new ArrayList<>();


    public static WorkoutHistory create(WorkoutHistoryDto historyDto, Member member, Member trainer) {
        return WorkoutHistory.builder()
                .workoutHistoryId(historyDto.getWorkoutHistoryId())
                .content(historyDto.getContent())
                .member(member)
                .trainer(trainer)
                .build();
    }

    public void updateContent(String content){
        this.content = content;
    }

    public void updateLikeCnt(Long likeCnt){
        this.likeCnt = likeCnt;
    }

    public void updateCommentCnt(Long commentCnt){
        this.commentCnt = commentCnt;
    }

    public void deleteWorkoutHistory() {
        this.delYn = true;
        this.deleteFiles();
        this.deleteComments();
    }

    public void deleteComments() {
        this.historyComments.forEach(WorkoutHistoryComment::deleteComment);
    }

    public void deleteFiles() {
        this.historyFiles.forEach(WorkoutHistoryFile::deleteWorkoutHistoryFile);
    }

}

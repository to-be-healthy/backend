package com.tobe.healthy.workout.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "workout_history_files")
@Builder
@Getter
public class WorkoutHistoryFiles extends BaseTimeEntity<WorkoutHistoryFiles, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String fileUrl;
    private int fileOrder;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean delYn = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_history_id")
    private WorkoutHistory workoutHistory;

    public static WorkoutHistoryFiles create(WorkoutHistory history, String fileUrl, int fileOrder) {
        return WorkoutHistoryFiles.builder()
                .workoutHistory(history)
                .fileUrl(fileUrl)
                .fileOrder(fileOrder)
                .build();
    }

    public void deleteWorkoutHistoryFile() {
        this.delYn = true;
    }

}

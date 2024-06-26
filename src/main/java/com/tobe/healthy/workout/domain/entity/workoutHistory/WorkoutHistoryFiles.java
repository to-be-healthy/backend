package com.tobe.healthy.workout.domain.entity.workoutHistory;

import com.tobe.healthy.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "workout_history_files")
@Builder
@Getter
@ToString
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
    @ToString.Exclude
    private WorkoutHistory workoutHistory;

    private String fileName;

    public static WorkoutHistoryFiles create(WorkoutHistory history, String fileUrl, int fileOrder) {
        return WorkoutHistoryFiles.builder()
                .workoutHistory(history)
                .fileUrl(fileUrl)
                .fileOrder(fileOrder)
                .fileName(getFileName(fileUrl))
                .build();
    }

    private static String getFileName(String url) {
        String[] arr = url.split("/");
        return arr[arr.length - 1];
    }

    public void deleteWorkoutHistoryFile() {
        this.delYn = true;
    }

}

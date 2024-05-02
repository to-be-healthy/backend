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
@Table(name = "workout_history_file")
@Builder
@Getter
public class WorkoutHistoryFile extends BaseTimeEntity<WorkoutHistoryFile, Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String fileName;
    private String originalName;

    @Column(name = "file_ext")
    private String extension;

    private Long fileSize;
    private String fileUrl;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean delYn = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_history_id")
    private WorkoutHistory workoutHistory;

    public static WorkoutHistoryFile create(String savedFileName, String originalName, String extension, Long fileSize, WorkoutHistory history, String fileUrl) {
        return WorkoutHistoryFile.builder()
                .fileName(savedFileName)
                .originalName(originalName)
                .extension(extension)
                .fileSize(fileSize)
                .workoutHistory(history)
                .fileUrl(fileUrl)
                .build();
    }

    public void deleteWorkoutHistoryFile() {
        this.delYn = true;
    }

}

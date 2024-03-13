package com.tobe.healthy.file.domain.entity;

import com.tobe.healthy.common.BaseTimeEntity;
import com.tobe.healthy.workout.domain.entity.WorkoutHistory;
import jakarta.persistence.*;
import lombok.*;
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

    private String filePath;
    private Long fileSize;

    @ColumnDefault("'N'")
    @Builder.Default
    private char delYn = 'N';

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_history_id")
    private WorkoutHistory workoutHistory;

    public static WorkoutHistoryFile create(String savedFileName, String originalName, String extension, String filePath, Long fileSize, WorkoutHistory history) {
        return WorkoutHistoryFile.builder()
                .fileName(savedFileName)
                .originalName(originalName)
                .extension(extension)
                .filePath(filePath)
                .fileSize(fileSize)
                .workoutHistory(history)
                .build();
    }

    public void deleteWorkoutHistoryFile() {
        this.delYn = 'Y';
    }

}

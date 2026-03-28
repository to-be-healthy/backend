package com.tobe.healthy.lessonhistory.domain.dto.out;

import com.tobe.healthy.common.LessonTimeFormatter;
import com.tobe.healthy.lessonhistory.domain.entity.LessonAttendanceStatus;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles;
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryReadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveLessonHistoryByDateCondResult {

    private Long id;
    private String title;
    private String content;
    private Integer commentTotalCount;
    private LocalDateTime createdAt;
    private Long studentId;
    private String student;
    private String trainer;
    private String trainerProfile;
    private Long scheduleId;
    private String lessonDt;
    private String lessonTime;
    private String attendanceStatus;
    private LessonHistoryReadStatus feedbackChecked;
    @Builder.Default
    private List<LessonHistoryFileResults> files = new ArrayList<>();

    public static RetrieveLessonHistoryByDateCondResult top1From(LessonHistory entity) {
        if (entity == null) {
            return null;
        }
        return RetrieveLessonHistoryByDateCondResult.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .commentTotalCount((int) entity.getLessonHistoryComment().stream().filter(c -> !c.isDelYn()).count())
                .createdAt(entity.getCreatedAt())
                .studentId(entity.getStudent() != null ? entity.getStudent().getId() : null)
                .student(entity.getStudent() != null ? entity.getStudent().getName() : null)
                .trainer(entity.getTrainer() != null ? entity.getTrainer().getName() + " 트레이너" : null)
                .trainerProfile(entity.getTrainer() != null && entity.getTrainer().getMemberProfile() != null ? entity.getTrainer().getMemberProfile().getFileUrl() : null)
                .scheduleId(entity.getSchedule() != null ? entity.getSchedule().getId() : null)
                .lessonDt(LessonTimeFormatter.formatLessonDt(entity.getSchedule() != null ? entity.getSchedule().getLessonDt() : null))
                .lessonTime(LessonTimeFormatter.formatLessonTime(
                        entity.getSchedule() != null ? entity.getSchedule().getLessonStartTime() : null,
                        entity.getSchedule() != null ? entity.getSchedule().getLessonEndTime() : null))
                .attendanceStatus(validateAttendanceStatus(
                        entity.getSchedule() != null ? entity.getSchedule().getLessonDt() : null,
                        entity.getSchedule() != null ? entity.getSchedule().getLessonEndTime() : null))
                .feedbackChecked(entity.getFeedbackChecked())
                .files(entity.getFiles().stream()
                        .map(LessonHistoryFileResults::from)
                        .sorted(Comparator.comparing(LessonHistoryFileResults::getCreatedAt))
                        .collect(Collectors.toList()))
                .build();
    }

    public static RetrieveLessonHistoryByDateCondResult from(LessonHistory entity) {
        return RetrieveLessonHistoryByDateCondResult.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .commentTotalCount((int) entity.getLessonHistoryComment().stream().filter(c -> !c.isDelYn()).count())
                .createdAt(entity.getCreatedAt())
                .studentId(entity.getStudent() != null ? entity.getStudent().getId() : null)
                .student(entity.getStudent() != null ? entity.getStudent().getName() : null)
                .trainer(entity.getTrainer() != null ? entity.getTrainer().getName() + " 트레이너" : null)
                .trainerProfile(entity.getTrainer() != null && entity.getTrainer().getMemberProfile() != null ? entity.getTrainer().getMemberProfile().getFileUrl() : null)
                .scheduleId(entity.getSchedule() != null ? entity.getSchedule().getId() : null)
                .lessonDt(LessonTimeFormatter.formatLessonDt(entity.getSchedule() != null ? entity.getSchedule().getLessonDt() : null))
                .lessonTime(LessonTimeFormatter.formatLessonTime(
                        entity.getSchedule() != null ? entity.getSchedule().getLessonStartTime() : null,
                        entity.getSchedule() != null ? entity.getSchedule().getLessonEndTime() : null))
                .attendanceStatus(validateAttendanceStatus(
                        entity.getSchedule() != null ? entity.getSchedule().getLessonDt() : null,
                        entity.getSchedule() != null ? entity.getSchedule().getLessonEndTime() : null))
                .feedbackChecked(entity.getFeedbackChecked())
                .files(entity.getFiles().stream()
                        .filter(f -> f.getLessonHistoryComment() == null)
                        .map(LessonHistoryFileResults::from)
                        .sorted(Comparator.comparing(LessonHistoryFileResults::getCreatedAt))
                        .collect(Collectors.toList()))
                .build();
    }

    private static String validateAttendanceStatus(LocalDate lessonDt, LocalTime lessonEndTime) {
        LocalDateTime lesson = LocalDateTime.of(lessonDt, lessonEndTime);
        if (LocalDateTime.now().isAfter(lesson)) {
            return LessonAttendanceStatus.ATTENDED.getDescription();
        }
        return LessonAttendanceStatus.ABSENT.getDescription();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonHistoryFileResults {
        private String fileUrl;
        private int fileOrder;
        private LocalDateTime createdAt;

        public static LessonHistoryFileResults from(LessonHistoryFiles entity) {
            return LessonHistoryFileResults.builder()
                    .fileUrl(entity.getFileUrl())
                    .fileOrder(entity.getFileOrder())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}

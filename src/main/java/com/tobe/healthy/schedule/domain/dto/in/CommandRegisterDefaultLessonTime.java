package com.tobe.healthy.schedule.domain.dto.in;

import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.error.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CommandRegisterDefaultLessonTime {

    @Schema(description = "시작 수업 시간", example = "10:00:00", type = "string")
    private LocalTime lessonStartTime = LocalTime.of(9, 0, 0);

    @Schema(description = "종료 수업 시간", example = "22:00:00", type = "string")
    private LocalTime lessonEndTime = LocalTime.of(18, 0, 0);

    @Schema(description = "시작 점심시간", example = "12:00:00", type = "string")
    private LocalTime lunchStartTime;

    @Schema(description = "종료 점심시간", example = "13:00:00", type = "string")
    private LocalTime lunchEndTime;

    private List<DayOfWeek> closedDays = new ArrayList<>();

    @Schema(description = "세션당 수업 시간", example = "30|60|90|120")
    @NotNull(message = "수업 시간을 입력해 주세요.")
    private Integer lessonTime;

    @Builder
    public CommandRegisterDefaultLessonTime(
            LocalTime lessonStartTime,
            LocalTime lessonEndTime,
            LocalTime lunchStartTime,
            LocalTime lunchEndTime,
            List<DayOfWeek> closedDays,
            Integer lessonTime
    ) {
        this.lessonStartTime = lessonStartTime != null ? lessonStartTime : LocalTime.of(9, 0, 0);
        this.lessonEndTime = lessonEndTime != null ? lessonEndTime : LocalTime.of(18, 0, 0);
        this.lunchStartTime = lunchStartTime;
        this.lunchEndTime = lunchEndTime;
        this.closedDays = closedDays != null ? closedDays : new ArrayList<>();
        this.lessonTime = lessonTime;
        validate();
    }

    public void setLessonStartTime(LocalTime lessonStartTime) {
        this.lessonStartTime = lessonStartTime != null ? lessonStartTime : LocalTime.of(9, 0, 0);
        validateIfReady();
    }

    public void setLessonEndTime(LocalTime lessonEndTime) {
        this.lessonEndTime = lessonEndTime != null ? lessonEndTime : LocalTime.of(18, 0, 0);
        validateIfReady();
    }

    public void setLunchStartTime(LocalTime lunchStartTime) {
        this.lunchStartTime = lunchStartTime;
        validateIfReady();
    }

    public void setLunchEndTime(LocalTime lunchEndTime) {
        this.lunchEndTime = lunchEndTime;
        validateIfReady();
    }

    public void setClosedDays(List<DayOfWeek> closedDays) {
        this.closedDays = closedDays != null ? closedDays : new ArrayList<>();
    }

    public void validate() {
        if (lessonStartTime.isAfter(lessonEndTime) || lessonStartTime.equals(lessonEndTime)) {
            throw new CustomException(ErrorCode.START_TIME_AFTER_END_TIME);
        }
        if (lunchStartTime != null && lunchEndTime != null && lunchStartTime.isAfter(lunchEndTime)) {
            throw new CustomException(ErrorCode.LUNCH_TIME_INVALID);
        }
        if (!isWithinRange(lessonStartTime, lessonEndTime)) {
            throw new IllegalArgumentException("근무 시간은 오전 6시부터 밤 12시까지 설정이 가능해요.");
        }
    }

    private boolean isWithinRange(LocalTime lessonStartTime, LocalTime lessonEndTime) {
        LocalTime maxLessonStartTime = LocalTime.of(6, 0);
        LocalTime maxLessonEndTime = LocalTime.MIDNIGHT;

        return isWithinRange(lessonStartTime, maxLessonStartTime, maxLessonEndTime)
                && isWithinRange(lessonEndTime, maxLessonStartTime, maxLessonEndTime);
    }

    private boolean isWithinRange(LocalTime time, LocalTime start, LocalTime end) {
        if (start.isBefore(end)) {
            return !time.isBefore(start) && !time.isAfter(end);
        } else {
            return !time.isBefore(start) || !time.isAfter(end);
        }
    }

    private void validateIfReady() {
        if (lessonStartTime != null && lessonEndTime != null) {
            validate();
        }
    }
}

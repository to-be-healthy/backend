package com.tobe.healthy.schedule.domain.dto.in;

import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.error.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
public class CommandRegisterSchedule {

    @Schema(description = "시작 수업 일자", example = "2024-04-01")
    private LocalDate lessonStartDt;

    @Schema(description = "종료 수업 일자", example = "2024-04-30")
    private LocalDate lessonEndDt;

    @Builder
    public CommandRegisterSchedule(LocalDate lessonStartDt, LocalDate lessonEndDt) {
        this.lessonStartDt = lessonStartDt;
        this.lessonEndDt = lessonEndDt;
        validateIfReady();
    }

    public void setLessonStartDt(LocalDate lessonStartDt) {
        this.lessonStartDt = lessonStartDt;
        validateIfReady();
    }

    public void setLessonEndDt(LocalDate lessonEndDt) {
        this.lessonEndDt = lessonEndDt;
        validateIfReady();
    }

    public void validate() {
        if (lessonStartDt.isAfter(lessonEndDt)) {
            throw new CustomException(ErrorCode.START_DATE_AFTER_END_DATE);
        }
        if (ChronoUnit.DAYS.between(lessonStartDt, lessonEndDt) > 31) {
            throw new CustomException(ErrorCode.SCHEDULE_LESS_THAN_31_DAYS);
        }
    }

    private void validateIfReady() {
        if (lessonStartDt != null && lessonEndDt != null) {
            validate();
        }
    }
}

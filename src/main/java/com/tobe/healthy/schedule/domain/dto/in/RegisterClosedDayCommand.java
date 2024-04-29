package com.tobe.healthy.schedule.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "일정 개별 등록 DTO")
public class RegisterClosedDayCommand {
    @Schema(description = "등록할 수업 일자", example = "2024-04-01")
    @NotNull(message = "등록할 수업 일자를 입력해 주세요.")
    private List<LocalDate> lessonDt;
}

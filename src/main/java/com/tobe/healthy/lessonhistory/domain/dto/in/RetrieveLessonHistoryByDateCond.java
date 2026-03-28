package com.tobe.healthy.lessonhistory.domain.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "조회 조건 DTO")
public class RetrieveLessonHistoryByDateCond {

    @Schema(description = "조회 날짜", example = "YYYY-MM", required = false)
    @Builder.Default
    private String searchDate = formatDate(LocalDate.now());

    private static String formatDate(LocalDate lessonDt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM", Locale.KOREAN);
        return lessonDt.format(formatter);
    }
}

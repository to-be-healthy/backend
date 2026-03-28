package com.tobe.healthy.lessonhistory.domain.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomRetrieveLessonHistoryByDateCondResult {

    private String studentName;
    private List<RetrieveLessonHistoryByDateCondResult> content;

    public static CustomRetrieveLessonHistoryByDateCondResult from(List<RetrieveLessonHistoryByDateCondResult> entity) {
        String studentName = entity.isEmpty() ? null : entity.get(0).getStudent();
        return CustomRetrieveLessonHistoryByDateCondResult.builder()
                .studentName(studentName)
                .content(entity)
                .build();
    }
}

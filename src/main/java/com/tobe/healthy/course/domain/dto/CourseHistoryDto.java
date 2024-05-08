package com.tobe.healthy.course.domain.dto;

import com.tobe.healthy.course.domain.entity.CourseHistory;
import com.tobe.healthy.course.domain.entity.CourseHistoryType;
import com.tobe.healthy.point.domain.entity.Calculation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class CourseHistoryDto {

    private Long courseHistoryId;
    private int cnt;
    private Calculation calculation;
    private CourseHistoryType type;
    private LocalDateTime createdAt;

    public static CourseHistoryDto from(CourseHistory courseHistory) {
        return CourseHistoryDto.builder()
                .courseHistoryId(courseHistory.getCourseHistoryId())
                .cnt(courseHistory.getCnt())
                .calculation(courseHistory.getCalculation())
                .type(courseHistory.getType())
                .createdAt(courseHistory.getCreatedAt())
                .build();
    }
}

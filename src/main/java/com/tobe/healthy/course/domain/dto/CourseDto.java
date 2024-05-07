package com.tobe.healthy.course.domain.dto;

import com.tobe.healthy.course.domain.entity.Course;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CourseDto {

    private Long courseId;
    private int totalLessonCnt;
    private int remainLessonCnt;
    private LocalDateTime createdAt;


    public static CourseDto from(Course course) {
        return CourseDto.builder()
                .courseId(course.getCourseId())
                .totalLessonCnt(course.getTotalLessonCnt())
                .remainLessonCnt(course.getRemainLessonCnt())
                .createdAt(course.getCreatedAt())
                .build();
    }
}

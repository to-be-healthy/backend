package com.tobe.healthy.course.domain.dto;

import com.tobe.healthy.course.domain.entity.Course;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class CourseDto {

    private Long courseId;
    private int lessonCnt;
    private int remainLessonCnt;
    private LocalDateTime createdAt;


    public static CourseDto from(Course course) {
        return CourseDto.builder()
                .courseId(course.getCourseId())
                .lessonCnt(course.getLessonCnt())
                .remainLessonCnt(course.getRemainLessonCnt())
                .createdAt(course.getCreatedAt())
                .build();
    }
}

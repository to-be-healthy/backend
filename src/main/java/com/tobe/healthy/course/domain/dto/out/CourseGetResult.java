package com.tobe.healthy.course.domain.dto.out;

import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.dto.CourseHistoryDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseGetResult {

    private CourseDto course;
    private String gymName;

    @Builder.Default
    private List<CourseHistoryDto> courseHistories = null;

    public static CourseGetResult create(CourseDto courseDto, List<CourseHistoryDto> courseHistoryDtos, String gymName) {
        return CourseGetResult.builder()
                .course(courseDto)
                .courseHistories(courseHistoryDtos)
                .gymName(gymName)
                .build();
    }
}

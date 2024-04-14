package com.tobe.healthy.course.domain.dto.out;

import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.course.domain.dto.CourseHistoryDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CourseGetResult {

    private CourseDto course;

    @Builder.Default
    private List<CourseHistoryDto> courseHistories = new ArrayList<>();

    public static CourseGetResult create(CourseDto courseDto, List<CourseHistoryDto> courseHistoryDtos) {
        return CourseGetResult.builder()
                .course(courseDto)
                .courseHistories(courseHistoryDtos)
                .build();
    }
}

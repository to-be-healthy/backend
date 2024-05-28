package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.course.domain.dto.CourseDto;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Data
@ToString
@Builder
public class FindMyScheduleWaitingResult {

    private CourseDto course;
    private List<MyScheduleWaiting> myScheduleWaitings;

    public static FindMyScheduleWaitingResult create(CourseDto course, List<MyScheduleWaiting> myScheduleWaitings) {
        return FindMyScheduleWaitingResult.builder()
                .course(course)
                .myScheduleWaitings(ObjectUtils.isEmpty(myScheduleWaitings) ? null : myScheduleWaitings)
                .build();
    }
}

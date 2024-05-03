package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.course.domain.dto.CourseDto;
import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class MyStandbyScheduleResponse {

    private CourseDto course;
    private List<MyStandbySchedule> myStandbySchedules;

    public static MyStandbyScheduleResponse create(CourseDto course, List<MyStandbySchedule> myStandbySchedules) {
        return MyStandbyScheduleResponse.builder()
                .course(course)
                .myStandbySchedules(ObjectUtils.isEmpty(myStandbySchedules) ? null : myStandbySchedules)
                .build();
    }
}

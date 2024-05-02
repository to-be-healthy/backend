package com.tobe.healthy.schedule.domain.dto.out;

import com.tobe.healthy.course.domain.dto.CourseDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Data
@Builder
public class MyReservationResponse {

    private final CourseDto course;
    private final List<MyReservation> reservations;

    public static MyReservationResponse create(CourseDto course, List<MyReservation> reservations) {
        return MyReservationResponse.builder()
                .course(course)
                .reservations(ObjectUtils.isEmpty(reservations) ? null : reservations)
                .build();
    }

}

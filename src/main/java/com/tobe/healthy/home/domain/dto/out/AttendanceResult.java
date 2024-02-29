package com.tobe.healthy.home.domain.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceResult {

    private long memberId;
    private int month;
    private long attendanceRate;

}

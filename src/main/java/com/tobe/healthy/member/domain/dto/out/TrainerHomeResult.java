package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.schedule.entity.out.TrainerTodayScheduleResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerHomeResult {
    private Long studentCount;
    private String top1StudentName;
    private TrainerTodayScheduleResponse todaySchedule;
}

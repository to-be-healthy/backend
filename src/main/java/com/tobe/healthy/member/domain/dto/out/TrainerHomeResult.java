package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.schedule.entity.out.TrainerTodayScheduleResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TrainerHomeResult {
    private Long studentCount;
    private List<MemberInTeamResult> bestStudents;
    private TrainerTodayScheduleResponse todaySchedule;
}

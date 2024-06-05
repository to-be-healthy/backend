package com.tobe.healthy.member.domain.dto.out;

import com.tobe.healthy.gym.domain.dto.out.GymDto;
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonDtResult;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class TrainerHomeResult {
    private Long studentCount;
    private List<MemberInTeamResult> bestStudents;
    private RetrieveTrainerScheduleByLessonDtResult todaySchedule;
    private GymDto gym;
    private Boolean redDotStatus;
}

package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import java.util.List;

public interface ScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond);
	List<ScheduleCommandResult> findAllByApplicantId(Long memberId);
}

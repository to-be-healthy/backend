package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import com.tobe.healthy.schedule.domain.dto.in.ScheduleSearchCond;
import java.util.List;

public interface ScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule(ScheduleSearchCond searchCond);
}

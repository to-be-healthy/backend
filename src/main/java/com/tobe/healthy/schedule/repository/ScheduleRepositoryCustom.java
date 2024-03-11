package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.dto.out.ScheduleCommandResult;
import java.util.List;

public interface ScheduleRepositoryCustom {
	List<ScheduleCommandResult> findAllSchedule();
}

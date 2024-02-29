package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.dto.ScheduleCommand;
import java.time.LocalDateTime;

public interface ScheduleRepositoryCustom {
	LocalDateTime registerSchedule(ScheduleCommand request);
}

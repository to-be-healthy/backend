package com.tobe.healthy.schedule.repository.student;

import com.tobe.healthy.schedule.domain.dto.out.MyStandbySchedule;

import java.util.List;

public interface StandByScheduleRepositoryCustom {
	List<MyStandbySchedule> findAllMyStandbySchedule(Long memberId);

}

package com.tobe.healthy.schedule.application;

import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandByScheduleRepository extends JpaRepository<StandBySchedule, Long> {

}

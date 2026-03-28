package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerScheduleRepository extends JpaRepository<Schedule, Long>, TrainerScheduleRepositoryCustom {
}

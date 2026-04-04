package com.tobe.healthy.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.schedule.domain.Schedule;

public interface TrainerScheduleRepository extends JpaRepository<Schedule, Long>, TrainerScheduleRepositoryCustom {
}

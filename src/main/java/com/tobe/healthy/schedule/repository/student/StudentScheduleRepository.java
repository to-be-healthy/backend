package com.tobe.healthy.schedule.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobe.healthy.schedule.domain.Schedule;

public interface StudentScheduleRepository extends JpaRepository<Schedule, Long>, StudentScheduleRepositoryCustom {

}

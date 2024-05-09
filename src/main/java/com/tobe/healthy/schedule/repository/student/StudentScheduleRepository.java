package com.tobe.healthy.schedule.repository.student;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentScheduleRepository extends JpaRepository<Schedule, Long>, StudentScheduleRepositoryCustom {

}

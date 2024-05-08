package com.tobe.healthy.schedule.repository.student;

import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentScheduleRepository extends JpaRepository<Schedule, Long>, StudentScheduleRepositoryCustom {

}

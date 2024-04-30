package com.tobe.healthy.schedule.application;

import com.tobe.healthy.schedule.domain.entity.StandBySchedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StandByScheduleRepository extends JpaRepository<StandBySchedule, Long> {
	Optional<StandBySchedule> findByScheduleIdAndMemberId(Long scheduleId, Long memberId);
	@EntityGraph(attributePaths = {"member"})
	Optional<StandBySchedule> findByScheduleId(Long scheduleId);
}

package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommonScheduleRepository extends JpaRepository<Schedule, Long> {

	@EntityGraph(attributePaths = {"applicant"})
	@Query("select s from Schedule s where s.applicant.id = :userId and s.id = :scheduleId and s.delYn = false")
	Optional<Schedule> findScheduleByApplicantId(Long userId, Long scheduleId);

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@EntityGraph(attributePaths = {"trainer"})
	@Query("select s from Schedule s where s.id = :scheduleId and s.reservationStatus = 'AVAILABLE' and s.applicant is null and s.delYn = false")
	Optional<Schedule> findAvailableScheduleById(Long scheduleId);
}

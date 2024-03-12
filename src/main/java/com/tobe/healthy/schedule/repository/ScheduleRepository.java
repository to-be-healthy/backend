package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

	Optional<Schedule> findByTrainerIdAndId(Long userId, Long scheduleId);

	Optional<Schedule> findByApplicantIdAndId(Long userId, Long scheduleId);

	List<Schedule> findAllByApplicantId(Long memberId);

	@Query("select s from Schedule s where s.id = :scheduleId and s.reservationStatus = 'COMPLETED' and s.applicant is not null and s.standBySchedule is null")
	Optional<Schedule> findAvailableStandById(Long scheduleId);

	@Query("select s from Schedule s where s.id = :scheduleId and s.reservationStatus = 'AVAILABLE' and s.applicant is null and s.standBySchedule is null")
	Optional<Schedule> findAvailableScheduleById(Long scheduleId);
}

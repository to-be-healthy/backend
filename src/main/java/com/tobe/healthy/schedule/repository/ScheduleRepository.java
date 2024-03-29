package com.tobe.healthy.schedule.repository;

import com.tobe.healthy.schedule.domain.entity.Schedule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

	@Query("select s from Schedule s where s.trainer.id = :userId and s.id = :scheduleId and s.delYn = false")
	Optional<Schedule> findScheduleByTrainerId(Long userId, Long scheduleId);

	@Query("select s from Schedule s where s.applicant.id = :userId and s.id = :scheduleId and s.delYn = false")
	Optional<Schedule> findScheduleByApplicantId(Long userId, Long scheduleId);

	@Query("select s from Schedule s left join fetch s.standBySchedule where s.id = :scheduleId and s.reservationStatus = 'COMPLETED' and s.applicant is not null and s.delYn = false")
	Optional<Schedule> findAvailableStandById(Long scheduleId);

	@Query("select s from Schedule s where s.id = :scheduleId and s.reservationStatus = 'AVAILABLE' and s.applicant is null and s.delYn = false")
	Optional<Schedule> findAvailableScheduleById(Long scheduleId);
}

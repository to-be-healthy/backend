package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.schedule.domain.entity.Schedule
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface TrainerScheduleRepository : JpaRepository<Schedule, Long>, TrainerScheduleRepositoryCustom {

    @Query("select s from Schedule s where s.trainer.id = :userId and s.id = :scheduleId and s.delYn = false")
    fun findScheduleByTrainerId(userId: Long?, scheduleId: Long?): Schedule?

    @EntityGraph(attributePaths = ["applicant"])
    @Query("select s from Schedule s where s.applicant.id = :userId and s.id = :scheduleId and s.delYn = false")
    fun findScheduleByApplicantId(userId: Long?, scheduleId: Long?): Schedule?

    @Query("select s from Schedule s left join fetch s.standBySchedule where s.id = :scheduleId and s.reservationStatus = 'COMPLETED' and s.applicant is not null and s.delYn = false")
    fun findAvailableStandById(scheduleId: Long?): Optional<Schedule>

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = ["trainer"])
    @Query("select s from Schedule s where s.id = :scheduleId and s.reservationStatus = 'AVAILABLE' and s.applicant is null and s.delYn = false")
    fun findAvailableScheduleById(scheduleId: Long?): Schedule?
}

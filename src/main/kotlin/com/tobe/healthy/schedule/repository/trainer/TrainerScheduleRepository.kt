package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.schedule.domain.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface TrainerScheduleRepository : JpaRepository<Schedule, Long>, TrainerScheduleRepositoryCustom {

    @Query("select s from Schedule s where s.trainer.id = :userId and s.id = :scheduleId and s.delYn = false")
    fun findScheduleByTrainerId(userId: Long, scheduleId: Long): Schedule?

    @Query("select s from Schedule s left join fetch s.scheduleWaiting where s.id = :scheduleId and s.reservationStatus = 'COMPLETED' and s.applicant is not null and s.delYn = false")
    fun findAvailableWaitingId(scheduleId: Long): Optional<Schedule>
}

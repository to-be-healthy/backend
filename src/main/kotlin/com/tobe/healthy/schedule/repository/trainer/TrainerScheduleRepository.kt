package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.schedule.domain.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface TrainerScheduleRepository : JpaRepository<Schedule, Long>, TrainerScheduleRepositoryCustom {

    @Query("select s from Schedule s where s.trainer.id = :trainerId and s.id = :scheduleId and s.delYn = false")
    fun findScheduleByTrainerId(scheduleId: Long, trainerId: Long): Schedule?

    @Query("select s from Schedule s left join fetch s.scheduleWaiting where s.id = :scheduleId and s.reservationStatus = 'COMPLETED' and s.applicant is not null and s.delYn = false")
    fun findAvailableWaitingId(scheduleId: Long): Optional<Schedule>
}

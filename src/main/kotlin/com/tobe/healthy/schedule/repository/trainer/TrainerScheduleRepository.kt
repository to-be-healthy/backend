package com.tobe.healthy.schedule.repository.trainer

import com.tobe.healthy.schedule.domain.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TrainerScheduleRepository : JpaRepository<Schedule, Long>, TrainerScheduleRepositoryCustom {

    @Query("select s from Schedule s where s.trainer.id = :trainerId and s.id = :scheduleId and s.delYn = false")
    fun findScheduleByTrainerId(scheduleId: Long, trainerId: Long): Schedule?
}

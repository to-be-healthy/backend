package com.tobe.healthy.schedule.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.DynamicUpdate
import java.time.DayOfWeek

@Entity
@DynamicUpdate
class TrainerScheduleClosedDaysInfo(

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_schedule_info_id")
    val trainerScheduleInfo: TrainerScheduleInfo,

    @Enumerated(STRING)
    var closedDays: DayOfWeek,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "trainer_schedule_closed_days_id")
    val id: Long = 0
) {

    companion object {
        fun registerClosedDay(dayOfWeek: DayOfWeek, trainerScheduleInfo: TrainerScheduleInfo): TrainerScheduleClosedDaysInfo {
            return TrainerScheduleClosedDaysInfo(
                trainerScheduleInfo = trainerScheduleInfo,
                closedDays = dayOfWeek
            )
        }
    }
}

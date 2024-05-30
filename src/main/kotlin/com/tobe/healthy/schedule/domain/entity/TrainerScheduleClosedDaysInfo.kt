package com.tobe.healthy.schedule.domain.entity

import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.DynamicUpdate
import java.time.DayOfWeek

@Entity
@DynamicUpdate
class TrainerScheduleClosedDaysInfo(

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_schedule_info_id")
    val trainerScheduleInfo: TrainerScheduleInfo? = null,

    @Enumerated(STRING)
    var closedDays: DayOfWeek,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "trainer_schedule_closed_days_id")
    val id: Long? = null
) {

    companion object {
        fun registerClosedDay(
            dayOfWeek: DayOfWeek,
            trainerScheduleInfo: TrainerScheduleInfo
        ): TrainerScheduleClosedDaysInfo {
            return TrainerScheduleClosedDaysInfo(
                trainerScheduleInfo = trainerScheduleInfo,
                closedDays = dayOfWeek
            )
        }
    }
}

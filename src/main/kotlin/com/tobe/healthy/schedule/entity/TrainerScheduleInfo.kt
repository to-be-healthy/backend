package com.tobe.healthy.schedule.entity

import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.LessonTime
import com.tobe.healthy.schedule.entity.`in`.RegisterDefaultLessonTimeRequest
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import lombok.ToString
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalTime

@Entity
@DynamicUpdate
@ToString
class TrainerScheduleInfo(
    var lessonStartTime: LocalTime,
    var lessonEndTime: LocalTime,
    var lunchStartTime: LocalTime? = null,
    var lunchEndTime: LocalTime? = null,
    @Enumerated(STRING)
    var lessonTime: LessonTime,

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    val trainer: Member,

    @OneToMany(fetch = LAZY, mappedBy = "trainerScheduleInfo", cascade = [ALL], orphanRemoval = true)
    var trainerScheduleClosedDays: MutableList<TrainerScheduleClosedDaysInfo>? = mutableListOf(),

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "trainer_schedule_info_id")
    val id: Long = 0
) {

    fun registerTrainerScheduleClosedDays(trainerScheduleClosedDays: List<TrainerScheduleClosedDaysInfo>) {
        this.trainerScheduleClosedDays?.addAll(trainerScheduleClosedDays)
    }

    fun changeDefaultLessonTime(request: RegisterDefaultLessonTimeRequest) {
        this.lessonStartTime = request.startTime
        this.lessonEndTime = request.endTime
        this.lunchStartTime = request.lunchStartTime
        this.lunchEndTime = request.lunchEndTime
        this.lessonTime = request.sessionTime
    }

    companion object {
        fun registerDefaultLessonTime(request: RegisterDefaultLessonTimeRequest, trainer: Member): TrainerScheduleInfo {
            return TrainerScheduleInfo(
                lessonStartTime = request.startTime,
                lessonEndTime = request.endTime,
                lunchStartTime = request.lunchStartTime,
                lunchEndTime = request.lunchEndTime,
                lessonTime = request.sessionTime,
                trainer = trainer
            )
        }
    }
}

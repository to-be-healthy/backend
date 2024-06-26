package com.tobe.healthy.schedule.domain.entity

import com.tobe.healthy.common.error.CustomException
import com.tobe.healthy.common.error.ErrorCode.INVALID_LESSON_TIME_DESCRIPTION
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.dto.`in`.CommandRegisterDefaultLessonTime
import com.tobe.healthy.schedule.domain.entity.LessonTime.ONE_HOUR
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalTime

@Entity
@DynamicUpdate
class TrainerScheduleInfo(
    var lessonStartTime: LocalTime,

    var lessonEndTime: LocalTime,

    var lunchStartTime: LocalTime? = null,

    var lunchEndTime: LocalTime? = null,

    @Enumerated(STRING)
    var lessonTime: LessonTime,

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    val trainer: Member? = null,

    @OneToMany(fetch = LAZY, mappedBy = "trainerScheduleInfo", cascade = [ALL], orphanRemoval = true)
    var trainerScheduleClosedDays: MutableList<TrainerScheduleClosedDaysInfo> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "trainer_schedule_info_id")
    val id: Long? = null
) {

    fun changeDefaultLessonTime(request: CommandRegisterDefaultLessonTime, closedDays: MutableList<TrainerScheduleClosedDaysInfo>) {
        this.lessonStartTime = request.lessonStartTime
        this.lessonEndTime = request.lessonEndTime
        this.lunchStartTime = request.lunchStartTime
        this.lunchEndTime = request.lunchEndTime
        this.lessonTime = fromDescription(request.lessonTime!!)
        this.trainerScheduleClosedDays.clear()
        this.trainerScheduleClosedDays.addAll(closedDays)
    }

    companion object {
        fun registerDefaultLessonTime(
            request: CommandRegisterDefaultLessonTime,
            trainer: Member
        ): TrainerScheduleInfo {
            return TrainerScheduleInfo(
                lessonStartTime = request.lessonStartTime,
                lessonEndTime = request.lessonEndTime,
                lunchStartTime = request.lunchStartTime,
                lunchEndTime = request.lunchEndTime,
                lessonTime = ONE_HOUR,
                trainer = trainer,
            )
        }
        fun fromDescription(description: Int): LessonTime {
            for (lessonTime in LessonTime.entries) {
                if (lessonTime.description == description) {
                    return lessonTime
                }
            }
            throw CustomException(INVALID_LESSON_TIME_DESCRIPTION)
        }
    }
}

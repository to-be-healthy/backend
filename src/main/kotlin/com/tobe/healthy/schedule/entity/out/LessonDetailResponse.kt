package com.tobe.healthy.schedule.entity.out

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.tobe.healthy.common.LessonDetailResponseSerializer
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class LessonResponse(
    val trainerName: String?,
    val schedule: Map<LocalDate?, List<LessonDetailResponse?>>
) {
    companion object {
        private const val DEFAULT_DURATION = 60.0
        fun from(schedule: MutableList<Schedule?>): LessonResponse {
            return schedule.let {
                val groupingData = schedule.groupBy { it?.lessonDt }
                    .mapValues { entry ->
                        entry.value.map { schedule ->
                            LessonDetailResponse(
                                scheduleId = schedule?.id,
                                duration = Duration.between(schedule?.lessonStartTime, schedule?.lessonEndTime).toMinutes() / DEFAULT_DURATION,
                                lessonStartTime = schedule?.lessonStartTime,
                                lessonEndTime = schedule?.lessonEndTime,
                                reservationStatus = schedule?.reservationStatus,
                                applicantId = schedule?.applicant?.id,
                                applicantName = schedule?.applicant?.name,
                                waitingStudentId = schedule?.scheduleWaiting?.firstOrNull()?.member?.id,
                                waitingStudentName = schedule?.scheduleWaiting?.firstOrNull()?.member?.name
                            )
                        }
                    }
                LessonResponse(
                    trainerName = schedule.firstOrNull()?.trainer?.name?.let { "${it} 트레이너" },
                    schedule = groupingData
                )
            }
        }
    }

    @JsonSerialize(using = LessonDetailResponseSerializer::class)
    data class LessonDetailResponse(
        val scheduleId: Long?,
        val duration: Double?,
        val lessonStartTime: LocalTime?,
        val lessonEndTime: LocalTime?,
        val reservationStatus: ReservationStatus?,
        val applicantId: Long?,
        val applicantName: String?,
        val waitingStudentId: Long?,
        val waitingStudentName: String?
    )
}
package com.tobe.healthy.schedule.entity.out

import com.tobe.healthy.schedule.domain.entity.Schedule
import java.time.LocalDate
import java.time.LocalTime

data class LessonResponse(
    val trainerName: String?,
    val schedule: Map<LocalDate?, List<LessonDetailResponse?>>
) {
    companion object {
        fun from(schedule: MutableList<Schedule?>): LessonResponse {
            return schedule.let {
                val groupingData = schedule.groupBy { it?.lessonDt }
                    .mapValues { entry ->
                        entry.value.map { schedule ->
                            LessonDetailResponse(
                                scheduleId = schedule?.id,
                                lessonStartTime = schedule?.lessonStartTime,
                                lessonEndTime = schedule?.lessonEndTime,
                                reservationStatus = schedule?.reservationStatus?.name,
                                applicantName = schedule?.applicant?.name,
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

    data class LessonDetailResponse(
        val scheduleId: Long?,
        val lessonStartTime: LocalTime?,
        val lessonEndTime: LocalTime?,
        val reservationStatus: String?,
        val applicantName: String?,
        val waitingStudentName: String?
    )
}

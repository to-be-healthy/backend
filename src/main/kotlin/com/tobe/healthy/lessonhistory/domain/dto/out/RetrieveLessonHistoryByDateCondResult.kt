package com.tobe.healthy.lessonhistory.domain.dto.out

import com.tobe.healthy.common.LessonTimeFormatter.formatLessonDt
import com.tobe.healthy.common.LessonTimeFormatter.formatLessonTime
import com.tobe.healthy.lessonhistory.domain.entity.LessonAttendanceStatus.ABSENT
import com.tobe.healthy.lessonhistory.domain.entity.LessonAttendanceStatus.ATTENDED
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryFiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class RetrieveLessonHistoryByDateCondResult(
    val id: Long?,
    val title: String?,
    val content: String?,
    val commentTotalCount: Int?,
    val createdAt: LocalDateTime?,
    val studentId: Long?,
    val student: String?,
    val trainer: String?,
    val scheduleId: Long?,
    val lessonDt: String?,
    val lessonTime: String?,
    val attendanceStatus: String?,
    val files: MutableList<LessonHistoryFileResults> = mutableListOf(),
) {

    companion object {

        fun top1From(entity: LessonHistory?) : RetrieveLessonHistoryByDateCondResult? {
            return entity?.let {
                return RetrieveLessonHistoryByDateCondResult(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    commentTotalCount = entity.lessonHistoryComment.count { !it.delYn },
                    createdAt = entity.createdAt,
                    studentId = entity.student?.id,
                    student = entity.student?.name,
                    trainer = "${entity.trainer?.name} 트레이너",
                    scheduleId = entity.schedule?.id,
                    lessonDt = formatLessonDt(entity.schedule?.lessonDt),
                    lessonTime = formatLessonTime(entity.schedule?.lessonStartTime, entity.schedule?.lessonEndTime),
                    attendanceStatus = validateAttendanceStatus(entity.schedule?.lessonDt, entity.schedule?.lessonEndTime),
                    files = entity.files.map { file -> LessonHistoryFileResults.from(file) }.sortedBy { file -> file.createdAt }.toMutableList()
                )
            }
        }

        fun from(entity: LessonHistory): RetrieveLessonHistoryByDateCondResult {
            entity.let {
                return RetrieveLessonHistoryByDateCondResult(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    commentTotalCount = entity.lessonHistoryComment.count { !it.delYn },
                    createdAt = entity.createdAt,
                    studentId = entity.student?.id,
                    student = entity.student?.name,
                    trainer = "${entity.trainer?.name} 트레이너",
                    scheduleId = entity.schedule?.id,
                    lessonDt = formatLessonDt(entity.schedule?.lessonDt),
                    lessonTime = formatLessonTime(entity.schedule?.lessonStartTime, entity.schedule?.lessonEndTime),
                    attendanceStatus = validateAttendanceStatus(entity.schedule?.lessonDt, entity.schedule?.lessonEndTime),
                    files = entity.files.filter { it.lessonHistoryComment == null }.map { file -> LessonHistoryFileResults.from(file) }.sortedBy { file -> file.createdAt }.toMutableList()
                )
            }
        }

        private fun validateAttendanceStatus(lessonDt: LocalDate?, lessonEndTime: LocalTime?): String {
            val lesson = LocalDateTime.of(lessonDt, lessonEndTime)
            if (LocalDateTime.now().isAfter(lesson)) {
                return ATTENDED.description
            }
            return ABSENT.description
        }
    }

    data class LessonHistoryFileResults(
        val fileUrl: String,
        val fileOrder: Int,
        val createdAt: LocalDateTime,
    ) {
        companion object {
            fun from(entity: LessonHistoryFiles): LessonHistoryFileResults {
                return LessonHistoryFileResults(
                    fileUrl = entity.fileUrl,
                    fileOrder = entity.fileOrder,
                    createdAt = entity.createdAt
                )
            }
        }
    }
}

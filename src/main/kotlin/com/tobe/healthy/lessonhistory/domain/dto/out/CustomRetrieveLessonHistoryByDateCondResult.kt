package com.tobe.healthy.lessonhistory.domain.dto.out

data class CustomRetrieveLessonHistoryByDateCondResult(
    val studentName: String?,
    val content: List<RetrieveLessonHistoryByDateCondResult?>
) {
    companion object {
        fun from(entity: List<RetrieveLessonHistoryByDateCondResult>): CustomRetrieveLessonHistoryByDateCondResult {
            return CustomRetrieveLessonHistoryByDateCondResult(
                studentName = entity.firstOrNull()?.student,
                content = entity
            )
        }
    }
}
package com.tobe.healthy.lessonhistory.domain.dto.out

data class CustomRetrieveLessonHistoryByDateCondResult(
    val content: List<RetrieveLessonHistoryByDateCondResult?>
) {
    companion object {
        fun from(entity: List<RetrieveLessonHistoryByDateCondResult>): CustomRetrieveLessonHistoryByDateCondResult {
            return CustomRetrieveLessonHistoryByDateCondResult(content = entity)
        }
    }
}
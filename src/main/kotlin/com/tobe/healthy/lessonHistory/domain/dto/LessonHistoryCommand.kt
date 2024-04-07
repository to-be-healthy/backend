package com.tobe.healthy.lessonHistory.domain.dto

import com.tobe.healthy.lessonHistory.domain.entity.LessonHistory
import com.tobe.healthy.lessonHistory.domain.entity.LessonHistoryComment

data class LessonHistoryCommandResult(
    val id: Long,
    val title: String,
    val content: String,
    val comment: MutableList<LessonHistoryCommentCommandResult>
) {

    companion object {
        fun from(entity: LessonHistory): LessonHistoryCommandResult {
            return LessonHistoryCommandResult(
                id = entity.id ?: throw IllegalArgumentException("LessonHistory ID cannot be null"),
                title = entity.title,
                content = entity.content,
                comment = entity.lessonHistoryComment?.map { LessonHistoryCommentCommandResult.from(it) }
                    ?.toMutableList()
                    ?: mutableListOf()
            )
        }
    }

    data class LessonHistoryCommentCommandResult(
        val id: Long,
        val content: String,
        val writer: Long,
        val order: Int,
        val parentId: Long?
    ) {
        companion object {
            fun from(entity: LessonHistoryComment): LessonHistoryCommentCommandResult {
                return LessonHistoryCommentCommandResult(
                    id = entity.id ?: throw IllegalArgumentException("Comment ID cannot be null"),
                    content = entity.content,
                    writer = entity.writer.id,
                    order = entity.order,
                    parentId = entity.parentId?.id
                )
            }
        }
    }
}
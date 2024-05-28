package com.tobe.healthy.lessonhistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.lessonhistory.domain.dto.out.CommandUploadFileResult
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import lombok.ToString

@Entity
@ToString
class LessonHistoryFiles(
    val fileUrl: String,

    val fileOrder: Int,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    val member: Member? = null,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_id")
    @ToString.Exclude
    val lessonHistory: LessonHistory? = null,

    @ManyToOne(fetch = LAZY, cascade = [PERSIST])
    @JoinColumn(name = "lesson_history_comment_id")
    @ToString.Exclude
    val lessonHistoryComment: LessonHistoryComment? = null,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_files_id")
    val id: Long = 0
) : BaseTimeEntity<LessonHistoryFiles, Long>() {

    companion object {
        fun create(member: Member, fileUrl: String, fileOrder: Int): LessonHistoryFiles {
            return LessonHistoryFiles(
                member = member,
                fileUrl = fileUrl,
                fileOrder = fileOrder
            )
        }

        fun from(files: CommandUploadFileResult, lessonHistory: LessonHistory?, lessonHistoryComment: LessonHistoryComment?, writer: Member?): LessonHistoryFiles {
            return LessonHistoryFiles(
                fileUrl = files.fileUrl,
                fileOrder = files.fileOrder,
                member = writer,
                lessonHistory = lessonHistory,
                lessonHistoryComment = lessonHistoryComment
            )
        }
    }
}


package com.tobe.healthy.lessonhistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY

@Entity
class LessonHistoryFiles(
    val fileUrl: String,

    val fileOrder: Int,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    val member: Member? = null,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_id")
    val lessonHistory: LessonHistory? = null,

    @ManyToOne(fetch = LAZY, cascade = [ALL])
    @JoinColumn(name = "lesson_history_comment_id")
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
    }
}


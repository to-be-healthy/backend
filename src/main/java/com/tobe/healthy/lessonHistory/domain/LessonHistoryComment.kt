package com.tobe.healthy.lessonHistory.domain

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY

@Entity
class LessonHistoryComment(

    val parentId: Int? = null,

    val order: Int,

    val content: String,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    val writer: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_id")
    val lessonHistory: LessonHistory,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_comment_id")
    val id: Long? = null
) : BaseTimeEntity<LessonHistoryComment, Long>()
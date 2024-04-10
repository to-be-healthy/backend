package com.tobe.healthy.lessonHistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.file.domain.entity.AwsS3File
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
class LessonHistoryComment(

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    val parentId: LessonHistoryComment? = null,

    val order: Int,

    var content: String,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    val writer: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_id")
    val lessonHistory: LessonHistory,

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistoryComment")
    val files: MutableList<AwsS3File> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_comment_id")
    val id: Long = 0
) : BaseTimeEntity<LessonHistoryComment, Long>() {

    fun updateLessonHistoryComment(content: String) {
        this.content = content
    }
}
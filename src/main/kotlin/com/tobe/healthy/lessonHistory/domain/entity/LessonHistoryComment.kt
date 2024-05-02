package com.tobe.healthy.lessonHistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.file.AwsS3File
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.ColumnDefault
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

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistoryComment", cascade = [ALL])
    var files: MutableList<AwsS3File> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_comment_id")
    val id: Long = 0,

    @ColumnDefault("false")
    var delYn: Boolean = false,

    @Transient
    var replies: MutableList<LessonHistoryComment?> = mutableListOf(),

    ) : BaseTimeEntity<LessonHistoryComment, Long>() {

    fun updateLessonHistoryComment(content: String) {
        this.content = content
    }

    fun deleteComment() {
        this.delYn = true
    }
}

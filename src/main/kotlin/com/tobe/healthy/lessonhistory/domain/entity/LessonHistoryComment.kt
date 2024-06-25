package com.tobe.healthy.lessonhistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.member.domain.entity.Member
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import lombok.ToString
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicUpdate
import kotlin.jvm.Transient

@Entity
@DynamicUpdate
@ToString
class LessonHistoryComment(

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    val parent: LessonHistoryComment? = null,

    val order: Int,

    var content: String,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    @ToString.Exclude
    val writer: Member? = null,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lesson_history_id")
    @ToString.Exclude
    val lessonHistory: LessonHistory? = null,

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistoryComment", cascade = [ALL])
    @ToString.Exclude
    var files: MutableList<LessonHistoryFiles> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_comment_id")
    val id: Long? = null,

    @ColumnDefault("false")
    var delYn: Boolean = false,

    @Transient
    var replies: MutableList<LessonHistoryComment> = mutableListOf(),

    ) : BaseTimeEntity<LessonHistoryComment, Long>() {

    fun updateLessonHistoryComment(content: String) {
        this.content = content
    }

    fun deleteComment() {
        this.delYn = true
    }
}

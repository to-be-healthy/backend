package com.tobe.healthy.lessonhistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.lessonhistory.domain.entity.LessonHistoryReadStatus.UNREAD
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import lombok.ToString
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
@ToString
class LessonHistory(

    var title: String,

    var content: String,

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory", cascade = [ALL])
    @ToString.Exclude
    val lessonHistoryComment: MutableList<LessonHistoryComment> = mutableListOf(),

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory", cascade = [ALL], orphanRemoval = true)
    @ToString.Exclude
    var files: MutableList<LessonHistoryFiles> = mutableListOf(),

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    @ToString.Exclude
    val trainer: Member? = null,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    val student: Member? = null,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "schedule_id")
    @ToString.Exclude
    val schedule: Schedule? = null,

    @Enumerated(STRING)
    var feedbackChecked: LessonHistoryReadStatus = UNREAD,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_id")
    val id: Long? = null
) : BaseTimeEntity<LessonHistory, Long>() {

    fun updateLessonHistory(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun updateFeedbackStatus(feedbackStatus: LessonHistoryReadStatus) {
        this.feedbackChecked = feedbackStatus
    }

    companion object {
        fun register(title: String, content: String, student: Member, trainer: Member, schedule: Schedule): LessonHistory {
            return LessonHistory(
                title = title,
                content = content,
                trainer = trainer,
                student = student,
                schedule = schedule
            )
        }
    }
}

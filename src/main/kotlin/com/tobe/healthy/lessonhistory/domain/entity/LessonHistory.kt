package com.tobe.healthy.lessonhistory.domain.entity

import com.tobe.healthy.common.BaseTimeEntity
import com.tobe.healthy.lessonhistory.domain.dto.`in`.RegisterLessonHistoryCommand
import com.tobe.healthy.lessonhistory.domain.entity.FeedbackCheckStatus.UNREAD
import com.tobe.healthy.member.domain.entity.Member
import com.tobe.healthy.schedule.domain.entity.Schedule
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
class LessonHistory(

    var title: String,

    var content: String,

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory", cascade = [ALL])
    val lessonHistoryComment: MutableList<LessonHistoryComment> = mutableListOf(),

    @OneToMany(fetch = LAZY, mappedBy = "lessonHistory", cascade = [ALL])
    var file: MutableList<LessonHistoryFiles> = mutableListOf(),

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "trainer_id")
    val trainer: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id")
    val student: Member,

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "schedule_id")
    val schedule: Schedule,

    @Enumerated(STRING)
    var feedbackChecked: FeedbackCheckStatus = UNREAD,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lesson_history_id")
    val id: Long = 0
) : BaseTimeEntity<LessonHistory, Long>() {

    fun updateLessonHistory(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun updateFeedbackStatus(feedbackStatus: FeedbackCheckStatus) {
        this.feedbackChecked = feedbackStatus
    }

    companion object {
        fun register(request: RegisterLessonHistoryCommand, student: Member, trainer: Member, schedule: Schedule): LessonHistory {
            return LessonHistory(
                title = request.title!!,
                content = request.content!!,
                trainer = trainer,
                student = student,
                schedule = schedule
            )
        }
    }
}
